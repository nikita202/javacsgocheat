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

package org.xena.cs

import com.github.jonatino.OffsetManager.engineModule
import com.github.jonatino.OffsetManager.process
import com.github.jonatino.offsets.Offsets.*
import org.xena.plugin.utils.Vector

open class GameEntity : GameObject() {
	
	var model: Long = 0
		protected set
	
	var boneMatrix: Long = 0
		protected set
	
	var team: Int = 0
		protected set
	
	var isRunning: Boolean = false
		protected set
	
	var isDormant: Boolean = false
		protected set
	
	val viewOrigin = Vector()
	
	val velocity = Vector()
	
	val viewOffsets = Vector()
	
	val viewAngles = Vector()
	
	val bones = Vector()
	
	val punch = Vector()
	
	var isDead: Boolean = false
		protected set
	
	var isSpotted: Boolean = false
		protected set
	
	var isBombCarrier: Boolean = false
		protected set
	
	open fun update() {
		model = process().readUnsignedInt(address() + m_dwModel)
		boneMatrix = process().readUnsignedInt(address() + m_dwBoneMatrix)
		isRunning = process().readBoolean(address() + m_bMoveType)
		isDormant = process().readBoolean(address() + m_bDormant)
		team = process().readInt(address() + m_iTeamNum)
		
		viewOrigin.x = process().readFloat(address() + m_vecOrigin)
		viewOrigin.y = process().readFloat(address() + m_vecOrigin + 4)
		viewOrigin.z = process().readFloat(address() + m_vecOrigin + 8)
		
		velocity.x = process().readFloat(address() + m_vecVelocity)
		velocity.y = process().readFloat(address() + m_vecVelocity + 4)
		velocity.z = process().readFloat(address() + m_vecVelocity + 8)
		
		viewOffsets.x = process().readFloat(address() + m_vecViewOffset)
		viewOffsets.y = process().readFloat(address() + m_vecViewOffset + 4)
		viewOffsets.z = process().readFloat(address() + m_vecViewOffset + 8)
		
		val anglePointer = engineModule().readUnsignedInt(m_dwClientState.toLong())
		viewAngles.x = process().readFloat(anglePointer + m_dwViewAngles)
		viewAngles.y = process().readFloat(anglePointer + m_dwViewAngles + 4)
		viewAngles.z = process().readFloat(anglePointer + m_dwViewAngles + 8)
		
		val boneMatrix = process().readUnsignedInt(address() + m_dwBoneMatrix)
		if (boneMatrix > 0) {
			//Bones bone = Bones.roll();
			val bone = Bones.HEAD
			try {
				bones.x = process().readFloat(boneMatrix + (0x30 * bone.id) + 0x0C)
				bones.y = process().readFloat(boneMatrix + (0x30 * bone.id) + 0x1C)
				bones.z = process().readFloat(boneMatrix + (0x30 * bone.id) + 0x2C)
			} catch (e: Exception) {
				e.printStackTrace()
			}
			
		}
		
		punch.x = process().readFloat(address() + m_vecPunch)
		punch.y = process().readFloat(address() + m_vecPunch + 4)
		
		isDead = process().readByte(address() + m_lifeState) != 0
		isSpotted = process().readUnsignedInt(address() + m_bSpotted).toInt() != 0
	}
	
	val eyePos = viewOffsets.plus(viewOrigin)
	
	val type by lazy { EntityType.byAddress(address()) }
	
}