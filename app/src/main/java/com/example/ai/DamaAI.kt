package com.example.ai

import com.example.engine.DamaEngine
import com.example.model.AIDifficulty
import com.example.model.Board
import com.example.model.Move
import com.example.model.PieceType
import com.example.model.PlayerColor
import com.example.model.Position
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class DamaAI(
    private val engine: DamaEngine
) {

    /**
     * Finds the best move for the AI player according to its difficulty level.
     */
    fun findBestMove(board: Board, aiColor: PlayerColor, difficulty: AIDifficulty): Move? {
        val legalMoves = engine.getLegalMoves(board, aiColor)
        if (legalMoves.isEmpty()) return null
        if (legalMoves.size == 1) return legalMoves.first()

        return when (difficulty) {
            AIDifficulty.EASY -> selectEasyMove(board, legalMoves, aiColor)
            AIDifficulty.MEDIUM -> selectMediumMove(board, legalMoves, aiColor)
            AIDifficulty.HARD -> selectAlphaBetaMove(board, legalMoves, aiColor, depth = 3)
            AIDifficulty.EXPERT -> selectAlphaBetaMove(board, legalMoves, aiColor, depth = 4)
        }
    }

    private fun selectEasyMove(board: Board, moves: List<Move>, aiColor: PlayerColor): Move {
        // Beginner: Prioritizes captures if available, otherwise picks among top 3 moves with slight randomness
        val scoredMoves = moves.map { move ->
            val nextBoard = board.applyMove(move)
            val score = evaluateBoard(nextBoard, aiColor) + Random.nextInt(-30, 30)
            move to score
        }.sortedByDescending { it.second }

        val poolSize = min(3, scoredMoves.size)
        return scoredMoves[Random.nextInt(poolSize)].first
    }

    private fun selectMediumMove(board: Board, moves: List<Move>, aiColor: PlayerColor): Move {
        // Medium: Depth 2 minimax
        var bestMove = moves.first()
        var bestScore = Int.MIN_VALUE

        // Order moves: captures first
        val sortedMoves = sortMoves(moves)

        for (move in sortedMoves) {
            val nextBoard = board.applyMove(move)
            val score = minimax(
                board = nextBoard,
                depth = 1,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                isMaximizing = false,
                aiColor = aiColor
            )
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }
        return bestMove
    }

    private fun selectAlphaBetaMove(
        board: Board,
        moves: List<Move>,
        aiColor: PlayerColor,
        depth: Int
    ): Move {
        var bestMove = moves.first()
        var bestScore = Int.MIN_VALUE

        val sortedMoves = sortMoves(moves)

        for (move in sortedMoves) {
            val nextBoard = board.applyMove(move)
            val score = minimax(
                board = nextBoard,
                depth = depth - 1,
                alpha = bestScore,
                beta = Int.MAX_VALUE,
                isMaximizing = false,
                aiColor = aiColor
            )
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }
        return bestMove
    }

    private fun minimax(
        board: Board,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMaximizing: Boolean,
        aiColor: PlayerColor
    ): Int {
        val opponent = aiColor.opponent
        val currentPlayer = if (isMaximizing) aiColor else opponent

        val legalMoves = engine.getLegalMoves(board, currentPlayer)

        // Terminal condition or depth reached
        if (depth <= 0 || legalMoves.isEmpty()) {
            if (legalMoves.isEmpty()) {
                // If current player has no moves, they lost
                return if (isMaximizing) -10000 else 10000
            }
            return evaluateBoard(board, aiColor)
        }

        val sortedMoves = sortMoves(legalMoves)
        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (move in sortedMoves) {
                val nextBoard = board.applyMove(move)
                val eval = minimax(nextBoard, depth - 1, currentAlpha, currentBeta, false, aiColor)
                maxEval = max(maxEval, eval)
                currentAlpha = max(currentAlpha, eval)
                if (currentBeta <= currentAlpha) break // Alpha-Beta cut-off
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (move in sortedMoves) {
                val nextBoard = board.applyMove(move)
                val eval = minimax(nextBoard, depth - 1, currentAlpha, currentBeta, true, aiColor)
                minEval = min(minEval, eval)
                currentBeta = min(currentBeta, eval)
                if (currentBeta <= currentAlpha) break // Alpha-Beta cut-off
            }
            return minEval
        }
    }

    /**
     * Prioritizes moves for faster Alpha-Beta pruning:
     * Captures first, then promotions, then central moves.
     */
    private fun sortMoves(moves: List<Move>): List<Move> {
        return moves.sortedByDescending { move ->
            var priority = 0
            if (move.isCapture) priority += 1000 * move.captureCount
            if (move.isPromotion) priority += 500
            val centerDist = kotlin.math.abs(move.to.col - 3.5f) + kotlin.math.abs(move.to.row - 3.5f)
            priority -= (centerDist * 10).toInt()
            priority
        }
    }

    /**
     * Positional heuristic evaluation function tailored to Moroccan Draughts rules.
     */
    fun evaluateBoard(board: Board, aiColor: PlayerColor): Int {
        val opponent = aiColor.opponent
        var score = 0

        val aiPieces = board.getPiecesFor(aiColor)
        val opponentPieces = board.getPiecesFor(opponent)

        // 1. Material Evaluation
        for ((pos, piece) in aiPieces) {
            if (piece.type == PieceType.KING) {
                score += 320 // Kings are exceptionally valuable
                // King mobility & diagonal positioning
                score += getKingPositionalBonus(pos, board.size)
            } else {
                score += 100
                // Man advancement towards king row
                score += getManAdvancementBonus(pos, aiColor, board.size)
                // Center control
                score += getCenterBonus(pos, board.size)
                // Base defense bonus
                if (isHomeRow(pos, aiColor, board.size)) {
                    score += 15
                }
            }
        }

        for ((pos, piece) in opponentPieces) {
            if (piece.type == PieceType.KING) {
                score -= 320
                score -= getKingPositionalBonus(pos, board.size)
            } else {
                score -= 100
                score -= getManAdvancementBonus(pos, opponent, board.size)
                score -= getCenterBonus(pos, board.size)
                if (isHomeRow(pos, opponent, board.size)) {
                    score -= 15
                }
            }
        }

        return score
    }

    private fun getManAdvancementBonus(pos: Position, player: PlayerColor, boardSize: Int): Int {
        val rank = if (player == PlayerColor.LIGHT) {
            boardSize - 1 - pos.row
        } else {
            pos.row
        }
        return rank * 6
    }

    private fun getCenterBonus(pos: Position, boardSize: Int): Int {
        val mid = boardSize / 2
        val rowDist = kotlin.math.abs(pos.row - mid)
        val colDist = kotlin.math.abs(pos.col - mid)
        return (boardSize - (rowDist + colDist)) * 3
    }

    private fun getKingPositionalBonus(pos: Position, boardSize: Int): Int {
        // Main long diagonal control (from (0, 7) to (7, 0) or (0, 0) to (7, 7))
        val isMainDiagonal = pos.row == pos.col || (pos.row + pos.col == boardSize - 1)
        return if (isMainDiagonal) 20 else 5
    }

    private fun isHomeRow(pos: Position, player: PlayerColor, boardSize: Int): Boolean {
        return if (player == PlayerColor.LIGHT) pos.row == boardSize - 1 else pos.row == 0
    }
}
