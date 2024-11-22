/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btl_ltm.entity;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Admin
 */
public class FindGameResult implements Serializable{
    private Integer roomId;
    private List<Color>showColor;
    private List<Color>displayColor;

    public Integer getRoomId() {
        return roomId;
    }

    public List<Color> getShowColor() {
        return showColor;
    }

    public List<Color> getDisplayColor() {
        return displayColor;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public void setShowColor(List<Color> showColor) {
        this.showColor = showColor;
    }

    public void setDisplayColor(List<Color> displayColor) {
        this.displayColor = displayColor;
    }
    
}
