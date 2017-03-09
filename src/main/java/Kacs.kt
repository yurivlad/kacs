import org.ini4j.Wini

import java.awt.BorderLayout
import java.io.File
import java.io.FileWriter
import java.io.IOException

import javax.swing.JFrame

class Kacs {
    companion object {
        internal var title = "Bacs v1.12"

        internal var iternum = 0

        internal var scale = 1
        private var cores = 2
        internal var dimension = 300
        internal var itermax = 40000
        var actlim: Int = 0
        private val start = System.nanoTime()
        @Volatile internal lateinit var battlefield: Array<Array<Kacunit>>
        private lateinit var window: JFrame;

        @Throws(IOException::class)
        @JvmStatic fun main(args: Array<String>) {
            getParameters()
            initpainting()
            window = JFrame(title)
            window.setSize(dimension * scale + 15, dimension * scale + 38)
            window.defaultCloseOperation = 3
            window.layout = BorderLayout(1, 1)
            val playfield = Canvas()
            playfield.setSize(dimension * scale, dimension * scale)
            window.add(playfield)
            window.isVisible = true
            initbattle()
            val processes = Array<Iteration>(cores){
                val iter = Iteration()
                iter.isDaemon
                iter.start()
                iter
            }
            while (iternum < itermax) {
                try {
                    Thread.sleep(16L)
                    window.title = title + " Iteration " + iternum + " out of " + itermax + "(" + iternum * 100 / itermax + "% done)"
                    playfield.repaint()
                } catch (var22: InterruptedException) {
                    var22.printStackTrace()
                }

            }

            window.title = "$title Iteration $iternum out of $itermax(100% done)"
            val var26 = (System.nanoTime() - start).toFloat() / 1.0E9f

            try {
                Thread.sleep(1000L)
            } catch (var21: InterruptedException) {
                var21.printStackTrace()
            }

            try {
                var ex: Throwable? = null
                val var5: Any? = null

                try {
                    val writer = FileWriter("endgame.txt", false)

                    try {
                        var text = ""

                        for (i in 0..29) {
                            val that = battlefield[getRandom(0, dimension - 1)][getRandom(0, dimension - 1)]
                            text = text + "str=" + that.stats.str + " end=" + that.stats.end + " clr=" + that.stats.clr + " mut=" + that.stats.mut + " behaviour={ "

                            for (j in 0..Kacunit.actlim - 1) {
                                text = text + that.behaviour[j] + " "
                            }

                            text = text + "}\r\n"
                        }

                        text = text + "time: " + var26
                        writer.write(text)
                    } finally {
                        writer.close()

                    }
                } catch (var24: Throwable) {
                    ex = var24
                    throw ex
                }

            } catch (var25: Throwable) {
                println(var25.message)
            }

        }

        @Throws(IOException::class)
        internal fun initbattle() {
            battlefield[dimension / 2][dimension / 2].stats.clr = "FF0000"
            battlefield[dimension / 2][dimension / 2].direction = 0
            battlefield[dimension / 2][dimension / 2].stats.str = 1
            battlefield[dimension / 2][dimension / 2].stats.mut = 250
            battlefield[dimension / 2][dimension / 2].stats.end = 100
            battlefield[dimension / 2][dimension / 2].energy = 50.0f
            battlefield[dimension / 2][dimension / 2].behaviour[0] = Kacunit.actlim + 3
            getMoreParameters()

        }

        internal fun initpainting() {
            battlefield = Array(dimension) {
                Array(dimension) {
                    Kacunit()
                }
            }
        }

        fun getRandom(min: Int, max: Int): Int {
            return (Math.floor(Math.random() * (max - min + 1).toDouble()) + min.toDouble()).toInt()
        }

        @Throws(IOException::class)
        internal fun getParameters() {
            val ini = Wini(File("conf.ini"))
            cores = (ini.get("settings", "threads", Integer.TYPE) as Int).toInt()
            if (cores == 0) {
                cores = Runtime.getRuntime().availableProcessors()
            }

            dimension = (ini.get("settings", "dimension", Integer.TYPE) as Int).toInt()
            scale = (ini.get("settings", "scale", Integer.TYPE) as Int).toInt()
            itermax = (ini.get("settings", "iterations", Integer.TYPE) as Int).toInt()
            Kacunit.actlim = (ini.get("bacunit", "actlim", Integer.TYPE) as Int).toInt()
            Kacunit.relsence = (ini.get("bacunit", "relsence", Integer.TYPE) as Int).toInt()
            Kacunit.gainbase = (ini.get("bacunit", "gainbase", Integer.TYPE) as Int).toInt()
            Kacunit.lumus = (ini.get("bacunit", "light", java.lang.Boolean.TYPE) as Boolean)
        }

        @Throws(IOException::class)
        internal fun getMoreParameters() {
            val ini = Wini(File("conf.ini"))
            val that = battlefield[dimension / 2][dimension / 2]
            that.stats.str = (ini.get("bacunit", "str", Integer.TYPE) as Int).toInt()
            that.stats.mut = (ini.get("bacunit", "mut", Integer.TYPE) as Int).toInt()
            that.stats.end = (ini.get("bacunit", "end", Integer.TYPE) as Int).toInt()
            var beh = ini.get("bacunit", "behaviour", String::class.java)
            beh = beh.replace("move", Integer.toString(Kacunit.actlim))
            beh = beh.replace("turn", Integer.toString(Kacunit.actlim + 1))
            beh = beh.replace("eat", Integer.toString(Kacunit.actlim + 2))
            beh = beh.replace("gain", Integer.toString(Kacunit.actlim + 3))
            beh = beh.replace("attack", Integer.toString(Kacunit.actlim + 4))
            beh = beh.replace("observe", Integer.toString(Kacunit.actlim + 5))
            val behraw = beh.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            for (i in behraw.indices) {
                that.behaviour[i] = Integer.parseInt(behraw[i])
            }

        }
    }
}
