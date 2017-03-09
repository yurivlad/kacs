import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D

import javax.swing.JComponent

internal class Canvas : JComponent() {
    lateinit var g2d: Graphics2D

    public override fun paintComponent(g: Graphics) {
        this.g2d = g as Graphics2D

        for (i in 0..Kacs.dimension - 1) {
            for (j in 0..Kacs.dimension - 1) {
                this.g2d.paint = Color.decode("#" + Kacs.battlefield[i][j].stats.clr)
                if (Kacs.scale > 3) {
                    this.g2d.drawRect(i * Kacs.scale, j * Kacs.scale, Kacs.scale - 1, Kacs.scale - 1)
                    this.g2d.fillRect(i * Kacs.scale, j * Kacs.scale, Kacs.scale - 1, Kacs.scale - 1)
                } else {
                    this.g2d.drawRect(i * Kacs.scale, j * Kacs.scale, Kacs.scale - 1, Kacs.scale - 1)
                }
            }
        }

    }

    companion object {
        private val serialVersionUID = 1L
    }
}
