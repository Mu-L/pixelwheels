package com.greenyetilab.race;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.XmlReader;

/**
 * Display the high score table
 */
public class HighScorePane extends ScrollPane {
    private Cell mNewHighScoreCell = null;

    public HighScorePane() {
        super(null);
    }

    public HighScorePane(XmlReader.Element root) {
        super(null);
    }

    public void init(Skin skin, HighScoreTable highScoreTable) {
        init(skin, highScoreTable, -1);
    }

    public void init(Skin skin, HighScoreTable highScoreTable, int newHighScoreIndex) {
        Table table = new Table(skin);
        IntArray scores = highScoreTable.getScores();
        for (int idx = 0; idx < scores.size; ++idx) {
            String style = idx == newHighScoreIndex ? "newHighScore" : "highScore";
            Cell cell = table.add(String.format("%d.", idx + 1), style).right();
            if (idx == newHighScoreIndex) {
                mNewHighScoreCell = cell;
            }
            table.add(String.valueOf(scores.get(idx)), style).expandX().right();
            table.row();
        }
        setWidget(table);
    }

    @Override
    public void layout() {
        super.layout();
        if (mNewHighScoreCell != null) {
            float y = mNewHighScoreCell.getActorY();
            float height = mNewHighScoreCell.getActorHeight();
            scrollToCenter(0, y, getWidth(), height);
        }
    }
}