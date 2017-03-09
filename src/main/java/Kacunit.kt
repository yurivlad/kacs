import java.util.*

class Kacunit internal constructor() : Cloneable {
    internal var stats = StatList()
    internal var energy: Float = 0.toFloat()
    internal var light: Double = 0.toDouble()
    internal var direction: Int = 0
    internal var action = 0
    internal var x: Int = 0
    internal var y: Int = 0
    internal var dx: Int = 0
    internal var dy: Int = 0
    var quirks = IntArray(10)
    var behaviour: IntArray

    init {
        this.behaviour = IntArray(actlim)
        this.stats.clr = "000000"
    }

    fun act(x: Int, y: Int) {
        var done = false
        this.x = x
        this.y = y
        var i: Int
        if (lumus) {

            i = Math.abs(Kacs.dimension / 2 - x)
            val tocentery = Math.abs(Kacs.dimension / 2 - y)
            val tocenter = Math.sqrt((i * i + tocentery * tocentery).toDouble())
            val diag = Math.sqrt((Kacs.dimension * Kacs.dimension + Kacs.dimension * Kacs.dimension).toDouble())
            this.light = (diag / 2.0 - tocenter) / (diag / 2.0)
            this.light = if (this.light < 0.0) 0.0 else this.light
        } else {
            this.light = 1.0
        }

        if (this.energy < this.stats.end.toFloat()) {
            if (this.energy > 0.0f) {
                this.energy -= 1.0f + Math.abs(this.stats.str).toFloat() / 10.0f

                i = 0
                while (i < actlim && !done) {
                    this.action %= actlim
                    if (this.behaviour[this.action] == actlim) {
                        this.move()
                        done = true
                        ++this.action
                    } else if (this.behaviour[this.action] == actlim + 1) {
                        this.turn(this.behaviour[(this.action + actlim + 1) % actlim])
                        this.action += 2
                    } else if (this.behaviour[this.action] == actlim + 2) {
                        ++this.action
                        this.eat()
                        done = true
                    } else if (this.behaviour[this.action] == actlim + 3) {
                        ++this.action
                        this.gain()
                        done = true
                    } else if (this.behaviour[this.action] == actlim + 4) {
                        ++this.action
                        this.attack()
                        done = true
                    } else if (this.behaviour[this.action] == actlim + 5) {
                        this.action += (this.observe() + actlim) % actlim
                    } else {
                        this.action = this.behaviour[this.action]
                    }
                    ++i
                }
            } else {
                this.die(false)
            }
        } else if (!this.breed()) {
            this.die(true)
        }

    }

    internal fun setd(direction: Int) {
        this.dx = (this.x + dirx[direction] + Kacs.dimension) % Kacs.dimension
        this.dy = (this.y + diry[direction] + Kacs.dimension) % Kacs.dimension
    }

    internal fun lookup(): String {
        this.setd(this.direction)
        val that = Kacs.battlefield[this.dx][this.dy]
        return if (that.stats.clr == "FFFFFF") "corpse" else if (that.stats.clr == "000000") "empty" else if (Math.abs(this.stats.clr!!.compareTo(that.stats.clr!!)) < relsence) "relative" else "other"
    }

    internal fun observe(): Int {

        val var2 = this.lookup().hashCode()
        var var1 = lookup()
        when (var2) {
            -1354663044 -> if (var1 == "corpse") {
                return 4
            }
            -554435892 -> if (var1 == "relative") {
                return 2
            }
            106069776 -> if (var1 == "other") {
                return 3
            }
        }

        return 1
    }

    internal fun eat(): Boolean {
        if (this.lookup() == "corpse") {
            this.energy += Kacs.battlefield[this.dx][this.dy].energy / 2.0f
            Kacs.battlefield[this.dx][this.dy] = Kacunit()
            return true
        } else {
            return false
        }
    }

    internal fun gain(): Boolean {
        this.energy = (this.energy.toDouble() + gainbase.toDouble() * this.light).toFloat()
        return true
    }

    internal fun attack(): Boolean {
        if (this.lookup() == "other") {
            if (Kacs.getRandom(0, this.stats.str + Kacs.battlefield[this.dx][this.dy].stats.str) <= this.stats.str) {
                Kacs.battlefield[this.dx][this.dy].die(true)
            }

            return true
        } else {
            return false
        }
    }

    internal fun breed(): Boolean {
        val freespace = IntArray(8)
        var point = 0
        val direction = this.direction

        var spacenum: Int
        spacenum = 0
        while (spacenum < 8) {
            this.direction = spacenum
            if (this.lookup() == "empty") {
                freespace[point] = spacenum
                ++point
            }
            ++spacenum
        }

        this.direction = direction
        if (point > 0) {
            spacenum = Kacs.getRandom(0, point - 1)
            spacenum = freespace[spacenum]
            this.setd(spacenum)

            try {
                this.energy /= 2.0f
                val e = this.clone()
                Kacs.battlefield[this.dx][this.dy] = e
                Kacs.battlefield[this.dx][this.dy].action = 0
                Kacs.battlefield[this.dx][this.dy].direction = Kacs.getRandom(0, 7)
                val that = Kacs.battlefield[this.dx][this.dy]
                if (Kacs.getRandom(0, 1000) < this.stats.mut) {
                    if (that.stats.str > 1) {
                        that.stats.str += Kacs.getRandom(-1, 1)
                    } else {
                        that.stats.str += Kacs.getRandom(0, 1)
                    }

                    that.stats.end += Kacs.getRandom(-1, 1)
                    that.stats.clr = Integer.toHexString(Kacs.getRandom(1, 16777214))
                    that.stats.mut += Kacs.getRandom(-1, 1)
                    val mutnum = Kacs.getRandom(0, actlim - 1)
                    that.behaviour[mutnum] = Kacs.getRandom(0, actlim + comnum - 1)
                }
            } catch (var8: CloneNotSupportedException) {
                println("Объект не может быть клонированным.")
                var8.printStackTrace()
            }

            return true
        } else {
            return false
        }
    }

    internal fun move(): Boolean {
        if (this.lookup() == "empty") {
            Kacunit()
            val swap = Kacs.battlefield[this.dx][this.dy]
            Kacs.battlefield[this.dx][this.dy] = Kacs.battlefield[this.x][this.y]
            Kacs.battlefield[this.x][this.y] = swap
            return true
        } else {
            return false
        }
    }

    internal fun turn(dir: Int) {
        if (dir % 2 == 0) {
            this.direction = (this.direction + 1) % 8
        } else {
            this.direction = (this.direction + 7) % 8
        }

    }

    internal fun die(corpse: Boolean) {
        if (corpse) {
            this.stats.clr = "FFFFFF"
        } else {
            Kacs.battlefield[this.x][this.y] = Kacunit()
        }

    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Kacunit {
        val unit = super.clone() as Kacunit
        unit.stats = this.stats.clone()
        unit.behaviour = IntArray(actlim)
        unit.behaviour = Arrays.copyOf(this.behaviour, actlim)
        return unit
    }

    internal inner class StatList : Cloneable {
        var clr: String? = null
        var str: Int = 0
        var end: Int = 0
        var mut: Int = 0

        @Throws(CloneNotSupportedException::class)
        public override fun clone(): Kacunit.StatList {
            return super.clone() as Kacunit.StatList
        }
    }

    companion object {
        internal var actlim = 20
        internal var comnum = 6
        internal var relsence = 5
        internal var gainbase = 5
        internal var lumus = true
        internal val dirx = intArrayOf(0, 1, 1, 1, 0, -1, -1, -1)
        internal val diry = intArrayOf(-1, -1, 0, 1, 1, 1, 0, -1)
    }
}
