package com.example

import com.example.engine.DamaEngine
import com.example.model.Board
import com.example.model.GameRules
import com.example.model.GameStatus
import com.example.model.Move
import com.example.model.Piece
import com.example.model.PieceType
import com.example.model.PlayerColor
import com.example.model.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DamaEngineTest {

    private val engine = DamaEngine(GameRules(boardSize = 8))

    @Test
    fun testInitialBoardSetup() {
        val board = Board.initial(8)
        // 12 pieces for LIGHT (rows 5, 6, 7 on dark squares)
        assertEquals(12, board.countPieces(PlayerColor.LIGHT))
        // 12 pieces for DARK (rows 0, 1, 2 on dark squares)
        assertEquals(12, board.countPieces(PlayerColor.DARK))

        // Check front row of light pieces has valid forward diagonal moves
        val lightMoves = engine.getLegalMoves(board, PlayerColor.LIGHT)
        // Pieces on row 5: (5,0), (5,2), (5,4), (5,6)
        // (5,0) can move to (4,1)
        // (5,2) can move to (4,1) and (4,3)
        // (5,4) can move to (4,3) and (4,5)
        // (5,6) can move to (4,5) and (4,7)
        // Total = 7 moves
        assertEquals(7, lightMoves.size)
        assertTrue(lightMoves.all { !it.isCapture })
    }

    @Test
    fun testRegularManCapture() {
        // Light man at (4, 3), Dark man at (3, 2), square (2, 1) is empty
        val pieces = mapOf(
            Position(4, 3) to Piece(id = 1, player = PlayerColor.LIGHT, type = PieceType.MAN),
            Position(3, 2) to Piece(id = 2, player = PlayerColor.DARK, type = PieceType.MAN)
        )
        val board = Board.custom(8, pieces)

        val moves = engine.getLegalMoves(board, PlayerColor.LIGHT)
        // Capture is mandatory
        assertEquals(1, moves.size)
        val captureMove = moves.first()
        assertTrue(captureMove.isCapture)
        assertEquals(Position(4, 3), captureMove.from)
        assertEquals(Position(2, 1), captureMove.to)
        assertEquals(listOf(Position(3, 2)), captureMove.capturedPositions)
    }

    @Test
    fun testBackwardCaptureForMan() {
        // In Moroccan Draughts rules, pawn can capture backward:
        // Light man at (3, 2), Dark man at (4, 3), landing square (5, 4) is empty
        val pieces = mapOf(
            Position(3, 2) to Piece(id = 1, player = PlayerColor.LIGHT, type = PieceType.MAN),
            Position(4, 3) to Piece(id = 2, player = PlayerColor.DARK, type = PieceType.MAN)
        )
        val board = Board.custom(8, pieces)

        val moves = engine.getLegalMoves(board, PlayerColor.LIGHT)
        assertTrue(moves.isNotEmpty())
        val backwardCapture = moves.firstOrNull { it.to == Position(5, 4) }
        assertTrue("Man should be able to capture backwards in Moroccan Draughts", backwardCapture != null)
        assertEquals(listOf(Position(4, 3)), backwardCapture?.capturedPositions)
    }

    @Test
    fun testMultiJumpCapture() {
        // Light man at (5, 0)
        // Dark man 1 at (4, 1), empty landing at (3, 2)
        // Dark man 2 at (2, 3), empty landing at (1, 4)
        val pieces = mapOf(
            Position(5, 0) to Piece(id = 1, player = PlayerColor.LIGHT, type = PieceType.MAN),
            Position(4, 1) to Piece(id = 2, player = PlayerColor.DARK, type = PieceType.MAN),
            Position(2, 3) to Piece(id = 3, player = PlayerColor.DARK, type = PieceType.MAN)
        )
        val board = Board.custom(8, pieces)

        val moves = engine.getLegalMoves(board, PlayerColor.LIGHT)
        assertEquals(1, moves.size)
        val doubleJump = moves.first()
        assertEquals(2, doubleJump.captureCount)
        assertEquals(Position(1, 4), doubleJump.to)
        assertEquals(listOf(Position(4, 1), Position(2, 3)), doubleJump.capturedPositions)
    }

    @Test
    fun testMajorityCaptureRule() {
        // Light man at (5, 2)
        // Branch 1: Dark piece at (4, 1) with landing (3, 0) -> 1 capture
        // Branch 2: Dark piece at (4, 3) with landing (3, 4), then Dark piece at (2, 5) with landing (1, 6) -> 2 captures
        val pieces = mapOf(
            Position(5, 2) to Piece(id = 1, player = PlayerColor.LIGHT, type = PieceType.MAN),
            Position(4, 1) to Piece(id = 2, player = PlayerColor.DARK, type = PieceType.MAN),
            Position(4, 3) to Piece(id = 3, player = PlayerColor.DARK, type = PieceType.MAN),
            Position(2, 5) to Piece(id = 4, player = PlayerColor.DARK, type = PieceType.MAN)
        )
        val board = Board.custom(8, pieces)

        val moves = engine.getLegalMoves(board, PlayerColor.LIGHT)
        // With majority capture rule (standard in Moroccan Draughts), the player MUST pick the 2-capture sequence!
        assertEquals(1, moves.size)
        assertEquals(2, moves.first().captureCount)
        assertEquals(Position(1, 6), moves.first().to)
    }

    @Test
    fun testPromotionToKing() {
        // Light man at (1, 2) moves forward to row 0 (0, 1) or (0, 3)
        val pieces = mapOf(
            Position(1, 2) to Piece(id = 1, player = PlayerColor.LIGHT, type = PieceType.MAN)
        )
        val board = Board.custom(8, pieces)

        val moves = engine.getLegalMoves(board, PlayerColor.LIGHT)
        assertTrue(moves.isNotEmpty())
        assertTrue(moves.all { it.isPromotion })

        val promotedBoard = board.applyMove(moves.first())
        val pieceAtLanding = promotedBoard[moves.first().to]
        assertEquals(PieceType.KING, pieceAtLanding?.type)
    }

    @Test
    fun testFlyingKingMoveAndCapture() {
        // King at (7, 0) can fly along diagonal to (0, 7) if open
        val pieces = mapOf(
            Position(7, 0) to Piece(id = 1, player = PlayerColor.LIGHT, type = PieceType.KING),
            Position(3, 4) to Piece(id = 2, player = PlayerColor.DARK, type = PieceType.MAN)
        )
        val board = Board.custom(8, pieces)

        val moves = engine.getLegalMoves(board, PlayerColor.LIGHT)
        assertTrue(moves.all { it.isCapture })
        // Flying King jumps over (3, 4) and can land on any square beyond it: (2, 5), (1, 6), (0, 7)
        val landingSquares = moves.map { it.to }
        assertTrue(landingSquares.contains(Position(2, 5)))
        assertTrue(landingSquares.contains(Position(1, 6)))
        assertTrue(landingSquares.contains(Position(0, 7)))
    }

    @Test
    fun testVictoryWhenOpponentHasNoPieces() {
        val pieces = mapOf(
            Position(4, 3) to Piece(id = 1, player = PlayerColor.LIGHT, type = PieceType.MAN)
        )
        val board = Board.custom(8, pieces)
        val status = engine.evaluateGameStatus(board, PlayerColor.DARK)
        assertTrue(status is GameStatus.Victory)
        assertEquals(PlayerColor.LIGHT, (status as GameStatus.Victory).winner)
    }

    @Test
    fun testVictoryWhenOpponentHasNoLegalMoves() {
        // Dark man trapped in corner
        val pieces = mapOf(
            Position(0, 7) to Piece(id = 1, player = PlayerColor.DARK, type = PieceType.MAN),
            Position(1, 6) to Piece(id = 2, player = PlayerColor.LIGHT, type = PieceType.MAN),
            Position(2, 5) to Piece(id = 3, player = PlayerColor.LIGHT, type = PieceType.MAN)
        )
        val board = Board.custom(8, pieces)
        // Dark piece at (0,7) would move to (1,6) which is blocked, and cannot jump because (2,5) is occupied!
        val status = engine.evaluateGameStatus(board, PlayerColor.DARK)
        assertTrue(status is GameStatus.Victory)
        assertEquals(PlayerColor.LIGHT, (status as GameStatus.Victory).winner)
    }
}
