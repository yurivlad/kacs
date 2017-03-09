internal class Iteration : Thread() {

    override fun run() {
        while (Kacs.iternum < Kacs.itermax) {
            val dimension = Kacs.dimension

            for (i in 0..dimension * dimension - 1) {
                val x = Kacs.getRandom(0, dimension - 1)
                val y = Kacs.getRandom(0, dimension - 1)
                if (!Kacs.battlefield[x][y].stats.clr.equals("000000") && !Kacs.battlefield[x][y].stats.clr.equals("FFFFFF")) {
                    Kacs.battlefield[x][y].act(x, y)
                }
            }
            Kacs.iternum++
        }
    }
}
