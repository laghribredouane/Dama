package com.example.engine

import com.example.model.Board
import com.example.model.GameRules
import com.example.model.GameStatus
import com.example.model.Move
import com.example.model.Piece
import com.example.model.PieceType
import com.example.model.PlayerColor
import com.example.model.Position
import com.example.model.VictoryReason

class DamaEngine(
    val rules: GameRules = GameRules()
) {

    /**
     * Computes all legal moves for the given player on the current board.
     * Takes into account:
     * 1. Mandatory capture rule (if any capture exists, regular moves are disallowed)
     * 2. Majority capture rule (only capture sequences with the maximum number of captures are legal)
     */
    fun getLegalMoves(board: Board, player: PlayerColor): List<Move> {
        val allCaptures = getAllCaptures(board, player)

        if (allCaptures.isNotEmpty() && rules.mandatoryCapture) {
            return if (rules.majorityCapture) {
                val maxCaptures = allCaptures.maxOf { it.captureCount }
                allCaptures.filter { it.captureCount == maxCaptures }
            } else {
                allCaptures
            }
        }

        // If no captures or captures are not mandatory, return regular non-capturing moves
        return getAllRegularMoves(board, player)
    }

    /**
     * Checks if a player has any captures available.
     */
    fun hasCaptures(board: Board, player: PlayerColor): Boolean {
        return getAllCaptures(board, player).isNotEmpty()
    }

    /**
     * Generates all non-capturing regular moves for a player.
     */
    fun getAllRegularMoves(board: Board, player: PlayerColor): List<Move> {
        val moves = mutableListOf<Move>()
        val playerPieces = board.getPiecesFor(player)

        for ((pos, piece) in playerPieces) {
            when (piece.type) {
                PieceType.MAN -> moves.addAll(getManRegularMoves(board, pos, player))
                PieceType.KING -> moves.addAll(getKingRegularMoves(board, pos, player))
            }
        }
        return moves
    }

    private fun getManRegularMoves(board: Board, pos: Position, player: PlayerColor): List<Move> {
        val moves = mutableListOf<Move>()
        val forwardDelta = if (player == PlayerColor.LIGHT) -1 else 1
        val promotionRow = if (player == PlayerColor.LIGHT) 0 else board.size - 1

        val colOffsets = intArrayOf(-1, 1)
        for (dc in colOffsets) {
            val target = Position(pos.row + forwardDelta, pos.col + dc)
            if (target.isValid(board.size) && board[target] == null) {
                val isPromotion = target.row == promotionRow
                moves.add(
                    Move(
                        from = pos,
                        to = target,
                        intermediateSteps = listOf(pos, target),
                        isPromotion = isPromotion
                    )
                )
            }
        }
        return moves
    }

    private fun getKingRegularMoves(board: Board, pos: Position, player: PlayerColor): List<Move> {
        val moves = mutableListOf<Move>()
        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 1),
            Pair(1, -1), Pair(1, 1)
        )

        for ((dr, dc) in directions) {
            var step = 1
            while (true) {
                val target = Position(pos.row + dr * step, pos.col + dc * step)
                if (!target.isValid(board.size)) break
                if (board[target] != null) break // Blocked by any piece

                moves.add(
                    Move(
                        from = pos,
                        to = target,
                        intermediateSteps = listOf(pos, target),
                        isPromotion = false
                    )
                )

                // If not flying king, can only move 1 step
                if (!rules.flyingKing) break
                step++
            }
        }
        return moves
    }

    /**
     * Generates all multi-jump capture sequences for a player.
     */
    fun getAllCaptures(board: Board, player: PlayerColor): List<Move> {
        val captures = mutableListOf<Move>()
        val playerPieces = board.getPiecesFor(player)

        for ((pos, piece) in playerPieces) {
            findCapturesForPiece(
                board = board,
                startPos = pos,
                currentPos = pos,
                piece = piece,
                capturedSoFar = emptyList(),
                stepsSoFar = listOf(pos),
                results = captures
            )
        }
        return captures
    }

    private fun findCapturesForPiece(
        board: Board,
        startPos: Position,
        currentPos: Position,
        piece: Piece,
        capturedSoFar: List<Position>,
        stepsSoFar: List<Position>,
        results: MutableList<Move>
    ) {
        val singleJumps = if (piece.type == PieceType.MAN) {
            getManSingleJumps(board, currentPos, piece.player, capturedSoFar)
        } else {
            getKingSingleJumps(board, currentPos, piece.player, capturedSoFar)
        }

        if (singleJumps.isEmpty()) {
            if (capturedSoFar.isNotEmpty()) {
                val promotionRow = if (piece.player == PlayerColor.LIGHT) 0 else board.size - 1
                val isPromoted = piece.type == PieceType.MAN && currentPos.row == promotionRow
                results.add(
                    Move(
                        from = startPos,
                        to = currentPos,
                        capturedPositions = capturedSoFar,
                        intermediateSteps = stepsSoFar,
                        isPromotion = isPromoted
                    )
                )
            }
            return
        }

        for (jump in singleJumps) {
            // Check if piece reaches promotion row during jump
            val promotionRow = if (piece.player == PlayerColor.LIGHT) 0 else board.size - 1
            val reachedKingRow = piece.type == PieceType.MAN && jump.landingPos.row == promotionRow

            val nextPiece = if (reachedKingRow && !rules.flyingKing) {
                piece.copy(type = PieceType.KING)
            } else {
                piece
            }

            findCapturesForPiece(
                board = board,
                startPos = startPos,
                currentPos = jump.landingPos,
                piece = nextPiece,
                capturedSoFar = capturedSoFar + jump.capturedPos,
                stepsSoFar = stepsSoFar + jump.landingPos,
                results = results
            )
        }
    }

    private data class SingleJump(
        val capturedPos: Position,
        val landingPos: Position
    )

    private fun getManSingleJumps(
        board: Board,
        pos: Position,
        player: PlayerColor,
        alreadyCaptured: List<Position>
    ): List<SingleJump> {
        val jumps = mutableListOf<SingleJump>()
        val forwardDelta = if (player == PlayerColor.LIGHT) -1 else 1

        val rowDirections = if (rules.pawnCanCaptureBackwards) {
            intArrayOf(-1, 1)
        } else {
            intArrayOf(forwardDelta)
        }
        val colOffsets = intArrayOf(-1, 1)

        for (dr in rowDirections) {
            for (dc in colOffsets) {
                val victimPos = Position(pos.row + dr, pos.col + dc)
                val landPos = Position(pos.row + 2 * dr, pos.col + 2 * dc)

                if (landPos.isValid(board.size)) {
                    val victim = board[victimPos]
                    val isLandingEmpty = board[landPos] == null || landPos == pos

                    if (victim != null &&
                        victim.player != player &&
                        !alreadyCaptured.contains(victimPos) &&
                        isLandingEmpty
                    ) {
                        jumps.add(SingleJump(capturedPos = victimPos, landingPos = landPos))
                    }
                }
            }
        }
        return jumps
    }

    private fun getKingSingleJumps(
        board: Board,
        pos: Position,
        player: PlayerColor,
        alreadyCaptured: List<Position>
    ): List<SingleJump> {
        val jumps = mutableListOf<SingleJump>()
        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 1),
            Pair(1, -1), Pair(1, 1)
        )

        for ((dr, dc) in directions) {
            if (rules.flyingKing) {
                var step = 1
                var victimFound: Position? = null

                while (true) {
                    val currentSquare = Position(pos.row + dr * step, pos.col + dc * step)
                    if (!currentSquare.isValid(board.size)) break

                    val pieceAtSquare = board[currentSquare]

                    if (victimFound == null) {
                        if (pieceAtSquare != null) {
                            if (pieceAtSquare.player != player && !alreadyCaptured.contains(currentSquare)) {
                                victimFound = currentSquare
                            } else {
                                // Blocked by own piece or already jumped piece
                                break
                            }
                        }
                    } else {
                        // After victim, any empty square is a valid landing square
                        if (pieceAtSquare == null || currentSquare == pos) {
                            jumps.add(SingleJump(capturedPos = victimFound, landingPos = currentSquare))
                        } else {
                            // Blocked by another piece after the victim
                            break
                        }
                    }
                    step++
                }
            } else {
                // Short-range King
                val victimPos = Position(pos.row + dr, pos.col + dc)
                val landPos = Position(pos.row + 2 * dr, pos.col + 2 * dc)

                if (landPos.isValid(board.size)) {
                    val victim = board[victimPos]
                    val isLandingEmpty = board[landPos] == null || landPos == pos

                    if (victim != null &&
                        victim.player != player &&
                        !alreadyCaptured.contains(victimPos) &&
                        isLandingEmpty
                    ) {
                        jumps.add(SingleJump(capturedPos = victimPos, landingPos = landPos))
                    }
                }
            }
        }
        return jumps
    }

    /**
     * Checks victory or draw status for the current board and active player.
     */
    fun evaluateGameStatus(
        board: Board,
        activePlayer: PlayerColor,
        movesWithoutCaptureOrPromotion: Int = 0
    ): GameStatus {
        val opponent = activePlayer.opponent
        val activePieces = board.countPieces(activePlayer)
        val opponentPieces = board.countPieces(opponent)

        if (activePieces == 0) {
            return GameStatus.Victory(opponent, VictoryReason.ALL_PIECES_CAPTURED)
        }
        if (opponentPieces == 0) {
            return GameStatus.Victory(activePlayer, VictoryReason.ALL_PIECES_CAPTURED)
        }

        val legalMoves = getLegalMoves(board, activePlayer)
        if (legalMoves.isEmpty()) {
            return GameStatus.Victory(opponent, VictoryReason.NO_LEGAL_MOVES)
        }

        // Draw by 40 moves rule without captures
        if (movesWithoutCaptureOrPromotion >= 80) { // 40 full moves = 80 half-moves
            return GameStatus.Draw("Règle des 40 coups sans capture ni promotion")
        }

        return GameStatus.Active
    }

    /**
     * Converts a Move to standard Draughts notation (e.g. "32-28" or "24x15x6").
     */
    fun formatMoveNotation(move: Move, boardSize: Int): String {
        fun posToNumber(pos: Position): Int {
            // Numbering playable squares 1..32 for 8x8, 1..50 for 10x10
            var count = 0
            for (r in 0 until boardSize) {
                for (c in 0 until boardSize) {
                    if ((r + c) % 2 != 0) {
                        count++
                        if (r == pos.row && c == pos.col) return count
                    }
                }
            }
            return 0
        }

        val fromNum = posToNumber(move.from)
        val toNum = posToNumber(move.to)

        return if (move.isCapture) {
            val steps = move.intermediateSteps.map { posToNumber(it) }
            steps.joinToString("x")
        } else {
            "$fromNum-$toNum"
        }
    }
}
