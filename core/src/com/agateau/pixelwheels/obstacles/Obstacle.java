/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.obstacles;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.TextureRegionProvider;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.gameobject.GameObjectAdapter;
import com.agateau.pixelwheels.racescreen.CollisionCategories;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.agateau.pixelwheels.utils.Box2DUtils;
import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class Obstacle extends GameObjectAdapter implements Disposable {
    private static final float LINEAR_DRAG = 90f;
    private static final float ANGULAR_DRAG = 2f;
    private final World mWorld;
    private final Body mBody;
    private final TextureRegion mRegion;
    private final float mRegionRadius;

    private final BodyRegionDrawer mBodyRegionDrawer = new BodyRegionDrawer();

    Obstacle(
            World box2DWorld,
            TextureRegionProvider provider,
            ObstacleDef obstacleDef,
            BodyDef bodyDef) {
        mWorld = box2DWorld;
        mBody = box2DWorld.createBody(bodyDef);
        mBody.createFixture(
                Box2DUtils.createBox2DShape(obstacleDef.shape, Constants.UNIT_FOR_PIXEL),
                obstacleDef.density);
        mRegion = obstacleDef.getImage(provider);
        mRegionRadius = Constants.UNIT_FOR_PIXEL * DrawUtils.getTextureRegionRadius(mRegion);

        Box2DUtils.setCollisionInfo(
                mBody,
                CollisionCategories.SOLID_BODIES,
                CollisionCategories.WALL
                        | CollisionCategories.RACER
                        | CollisionCategories.RACER_BULLET
                        | CollisionCategories.EXPLOSABLE);
        if (!obstacleDef.dynamic) {
            setStaticObstacleRestitution(mBody);
        }
    }

    @Override
    public void act(float delta) {
        Box2DUtils.applyDrag(mBody, LINEAR_DRAG);
        Box2DUtils.applyCircularDrag(mBody, ANGULAR_DRAG);
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel, Rectangle viewBounds) {
        if (zLevel != ZLevel.ON_GROUND && zLevel != ZLevel.GROUND) {
            return;
        }
        if (!AgcMathUtils.rectangleContains(viewBounds, getPosition(), mRegionRadius)) {
            return;
        }
        if (zLevel == ZLevel.ON_GROUND) {
            mBodyRegionDrawer.setBatch(batch);
            mBodyRegionDrawer.draw(mBody, mRegion);
        } else {
            mBodyRegionDrawer.setBatch(batch);
            mBodyRegionDrawer.drawShadow(mBody, mRegion);
        }
    }

    @Override
    public float getX() {
        return mBody.getPosition().x;
    }

    @Override
    public float getY() {
        return mBody.getPosition().y;
    }

    @Override
    public void dispose() {
        mWorld.destroyBody(mBody);
    }

    public static void setStaticObstacleRestitution(Body body) {
        float restitution = GamePlay.instance.borderRestitution / 10f;
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setRestitution(restitution);
        }
    }
}
