/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btl_ltm.entity;

import java.io.Serializable;

/**
 *
 * @author Admin
 */
public class Room implements Serializable{
    private static final long serialVersionUID = 2L;
    
    private Integer id;
    private Integer bestScore;
    private String winner;
    private Integer enable;
    private Integer totalCompleted;
    private Integer time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBestScore() {
        return bestScore;
    }

    public void setBestScore(Integer bestScore) {
        this.bestScore = bestScore;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(Integer totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
    
    
    
}
