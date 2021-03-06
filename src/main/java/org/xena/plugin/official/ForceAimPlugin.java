/*
 *    Copyright 2016 Jonathan Beaudoin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xena.plugin.official;

import org.xena.Indexer;
import org.xena.Settings;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.keylistener.NativeKeyUtils;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;
import org.xena.plugin.utils.Vector;

import java.awt.*;

@PluginManifest(name = "Aim Assist", description = "Helps you to stay on target.")
public final class ForceAimPlugin extends Plugin {
	
	private final AngleUtils aimHelper = new AngleUtils(this, Settings.FORCE_AIM_STRENGTH, 1.7F, 2.5F, 1.7F, 2.5F);
	private final Vector aim = new Vector();
	private final Vector lastaim = new Vector();
	private Player lastTarget = null;
	private boolean isOn = false;
	private boolean keyWasPressed = false;
	
	@Override
	public void pulse(ClientState clientState, Me me, Indexer<GameEntity> players) {
		if (NativeKeyUtils.isKeyDown(Settings.FORCE_AIM_TOGGLE)) {
			if (!keyWasPressed) isOn = !isOn;
			keyWasPressed = true;
		} else keyWasPressed = false;

		if (isOn && !me.getCursorEnabled()) {
			Player target = me.getClosestTarget(aimHelper, Settings.FORCE_AIM_FOV);
			if (lastTarget != null && target == null) {
				if (!lastTarget.isDead() && lastTarget.isSpotted()) {
					target = lastTarget;
				} else {
					lastTarget = null;
				}
			}
			
			if (target == null) {
				lastaim.reset();
				return;
			}
			
			if (aimHelper.canShoot(me, target)) {
				aimHelper.velocityComp(me, target, target.getBones());
				aimHelper.calculateAngle(me, me.getViewOrigin(), target.getBones(), aim);
				aimHelper.setAngleSmooth(aim, target.getViewAngles());
				
				lastTarget = target;
			} else {
				lastTarget = null;
			}
		} else {
			lastTarget = null;
		}
		sleep(10);
	}

	@Override
	public Color getColor() {
		if (enabled) {
			if (isOn) return Color.YELLOW; else return Color.GREEN;
		} else return Color.RED;
	}
}
