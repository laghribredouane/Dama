package com.example.model

class Board private constructor(
    val size: Int,
    private val grid: Map<Position, Piece>
) {
    val pieces: Map<Position, Piece>
        get() = grid

    operator fun get(position: Position): Piece? = grid[position]
    operator fun get(row: Int, col: Int): Piece? = grid[Position(row, col)]

    fun withPiece(position: Position, piece: Piece): Board {
        val newGrid = grid.toMutableMap()
        newGrid[position] = piece
        return Board(size, newGrid)
    }

    fun withoutPiece(position: Position): Board {
        val newGrid = grid.toMutableMap()
        newGrid.remove(position)
        return Board(size, newGrid)
    }

    fun applyMove(move: Move): Board {
        val movingPiece = grid[move.from] ?: return this
        val newGrid = grid.toMutableMap()

        // Remove from original position
        newGrid.remove(move.from)

        // Remove any captured pieces
        for (captured in move.capturedPositions) {
            newGrid.remove(captured)
        }

        // Place at destination, promoted if applicable
        val finalPiece = if (move.isPromotion) {
            movingPiece.copy(type = PieceType.KING)
        } else {
            movingPiece
        }
        newGrid[move.to] = finalPiece

        return Board(size, newGrid)
    }

    fun getPiecesFor(player: PlayerColor): List<Pair<Position, Piece>> {
        return grid.entries
            .filter { it.value.player == player }
            .map { it.key to it.value }
    }

    fun countPieces(player: PlayerColor): Int {
        return grid.values.count { it.player == player }
    }

    fun countKings(player: PlayerColor): Int {
        return grid.values.count { it.player == player && it.type == PieceType.KING }
    }

    fun countMen(player: PlayerColor): Int {
        return grid.values.count { it.player == player && it.type == PieceType.MAN }
    }

    companion object {
        fun initial(size: Int = 8): Board {
            val grid = mutableMapOf<Position, Piece>()
            var pieceId = 1
            val rowsPerPlayer = if (size == 10) 4 else 3

            // Dark pieces at the top
            for (row in 0 until rowsPerPlayer) {
                for (col in 0 until size) {
                    val pos = Position(row, col)
                    if (pos.isPlayableSquare()) {
                        grid[pos] = Piece(id = pieceId++, player = PlayerColor.DARK, type = PieceType.MAN)
                    }
                }
            }

            // Light pieces at the bottom
            val lightStartRow = size - rowsPerPlayer
            for (row in lightStartRow until size) {
                for (col in 0 until size) {
                    val pos = Position(row, col)
                    if (pos.isPlayableSquare()) {
                        grid[pos] = Piece(id = pieceId++, player = PlayerColor.LIGHT, type = PieceType.MAN)
                    }
                }
            }

            return Board(size, grid)
        }

        fun custom(size: Int, pieces: Map<Position, Piece>): Board {
            return Board(size, pieces)
        }
    }
}
