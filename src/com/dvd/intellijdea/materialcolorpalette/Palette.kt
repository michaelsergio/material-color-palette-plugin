/*
 * Copyright 2016 dvdandroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dvd.intellijdea.materialcolorpalette

import com.dvd.intellijdea.materialcolorpalette.Colors.allColors
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


/**
 * @author dvdandroid
 */
class Palette {

    var panel: JPanel? = null
    lateinit private var parentColors: JList<MaterialColor>
    lateinit private var childrenColors: JList<MaterialColor>
    private var lastIndex: Int = 0

    init {
        val listModel = DefaultListModel<MaterialColor>()
        for (allColor in allColors) {
            listModel.addElement(allColor[5])
        }

        parentColors.layoutOrientation = JList.HORIZONTAL_WRAP
        parentColors.cellRenderer = ColorRender(false)
        parentColors.model = listModel
        parentColors.visibleRowCount = 5

        childrenColors.layoutOrientation = JList.HORIZONTAL_WRAP
        childrenColors.cellRenderer = ColorRender(true)

        parentColors.addMouseListener(RightClickPopup())
        childrenColors.addMouseListener(RightClickPopup())
    }

    internal inner class RightClickPopup : MouseAdapter() {

        lateinit private var popup: JPopupMenu

        override fun mousePressed(e: MouseEvent?) {
            val list = e!!.source as JList<*>
            if (list.locationToIndex(e.point) == -1 && !e.isShiftDown) {
                list.clearSelection()
                return
            }

            val index = list.locationToIndex(e.point)

            if (index != -1 && !list.getCellBounds(index, index).contains(e.point)) return

            if (list === parentColors) {
                val children = DefaultListModel<MaterialColor>()

                for (i in 0 until allColors[index].size) {
                    children.addElement(allColors[index][i])
                }

                lastIndex = index

                childrenColors.model = children
                childrenColors.visibleRowCount = 4

                if (SwingUtilities.isRightMouseButton(e)) {
                    buildPopup(list, index)
                    popup.show(e.component, e.x, e.y)
                }
            } else {
                buildPopup(list, index)
                popup.show(e.component, e.x, e.y)
            }

            list.selectedIndex = index
        }

        private fun buildPopup(list: JList<*>, index: Int) {
            popup = JPopupMenu()

            if (list === childrenColors) {
                val hexThisItem = JMenuItem(String.format(PASTE, "this", "HEX code"))
                hexThisItem.addActionListener({ UtilsEnvironment.insertInEditor(allColors[lastIndex][index].hexCode) })

                val colorResThisItem = JMenuItem(String.format(PASTE, "this", "resource"))
                colorResThisItem.addActionListener({ UtilsEnvironment.insertInEditor(allColors[lastIndex][index].colorRes) })

                val clipboardThisItem = JMenuItem(String.format(COPY, "this"))
                clipboardThisItem.addActionListener({ copyToClipboard(allColors[lastIndex][index].hexCode) })

                popup.add(hexThisItem)
                popup.add(colorResThisItem)
                popup.add(clipboardThisItem)

                return
            }

            val hex500Item = JMenuItem(String.format(PASTE, "primary", "HEX code"))
            hex500Item.addActionListener({ UtilsEnvironment.insertInEditor(allColors[index][5].hexCode) })

            val colorRes500Item = JMenuItem(String.format(PASTE, "primary", "resource"))
            colorRes500Item.addActionListener({ UtilsEnvironment.insertInEditor(allColors[index][5].colorRes) })

            val clipboard500Item = JMenuItem(String.format(COPY, "primary"))
            clipboard500Item.addActionListener({ copyToClipboard(allColors[index][5].hexCode) })

            val hex700Item = JMenuItem(String.format(PASTE, "dark primary", "HEX code"))
            hex700Item.addActionListener({ UtilsEnvironment.insertInEditor(allColors[index][7].hexCode) })

            val colorRes700Item = JMenuItem(String.format(PASTE, "dark primary", "resource"))
            colorRes700Item.addActionListener({ UtilsEnvironment.insertInEditor(allColors[index][7].colorRes) })

            val clipboard700Item = JMenuItem(String.format(COPY, "dark primary"))
            clipboard700Item.addActionListener({ copyToClipboard(allColors[index][7].hexCode) })

            this.popup.add(hex500Item)
            this.popup.add(colorRes500Item)
            this.popup.add(clipboard500Item)
            this.popup.addSeparator()
            this.popup.add(hex700Item)
            this.popup.add(colorRes700Item)
            this.popup.add(clipboard700Item)

            if (index < 16) {
                val hexAccentItem = JMenuItem(String.format(PASTE, "accent", "HEX code"))
                hexAccentItem.addActionListener({ UtilsEnvironment.insertInEditor(allColors[index][11].hexCode) })

                val colorResAccentItem = JMenuItem(String.format(PASTE, "accent", "resource"))
                colorResAccentItem.addActionListener({ UtilsEnvironment.insertInEditor(allColors[index][11].colorRes) })

                val clipboardAccentItem = JMenuItem(String.format(COPY, "accent"))
                clipboardAccentItem.addActionListener({ copyToClipboard(allColors[index][11].hexCode) })

                popup.addSeparator()
                popup.add(hexAccentItem)
                popup.add(colorResAccentItem)
                popup.add(clipboardAccentItem)
            }
        }

        private fun copyToClipboard(colorRes: String) = Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(colorRes), null)
    }

    companion object {
        private val COPY = "Copy %s HEX color in clipboard"
        private val PASTE = "Paste %s color as %s"
    }

}