/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.hwbot.bench.bullet;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

import com.badlogic.gdx.math.Vector3;

/** @author xoppa */
public class DebugDrawer extends btIDebugDraw {
	public int debugMode = 0;
	public ShapeRenderer lineRenderer = new ShapeRenderer();

	@Override
	public void drawLine (Vector3 from, Vector3 to, Vector3 color) {
		lineRenderer.setColor(color.x, color.y, color.z, 1f);
		lineRenderer.line(from.x, from.y, from.z, to.x, to.y, to.z);
	}

	@Override
	public void drawContactPoint (Vector3 PointOnB, Vector3 normalOnB, float distance, int lifeTime, Vector3 color) {
	}

	@Override
	public void reportErrorWarning (String warningString) {
	}
	
	@Override
	public void draw3dText (Vector3 location, String textString) {
	}
	
	@Override
	public void setDebugMode (int debugMode) {
		this.debugMode = debugMode;
	}

	@Override
	public int getDebugMode () {
		return debugMode;
	}
	
	public void begin() {
		lineRenderer.begin(ShapeType.Line);
	}
	
	public void end() {
		lineRenderer.end();
	}
}
