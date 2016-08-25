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

import com.github.jonatino.OffsetManager.clientModule
import com.github.jonatino.OffsetManager.process
import com.github.jonatino.offsets.Offsets.*

open class Player : GameEntity() {
	
	var weaponIds = Array(8) { LongArray(2) }
	
	var activeWeapon = Weapon()
		protected set
	
	override fun update() {
		if (shouldUpdate()) {
			super.update()
			isBombCarrier = false
			
			for (i in weaponIds.indices) {
				val currentWeaponIndex = process().readUnsignedInt(address() + m_hMyWeapons.toLong() + ((i - 1) * 0x04).toLong()) and 0xFFF
				val weaponAddress = clientModule().readUnsignedInt(m_dwEntityList + (currentWeaponIndex - 1) * 0x10)
				
				if (weaponAddress > 0) {
					processWeapon(weaponAddress, i, false)
				}
			}
		}
	}
	
	open fun processWeapon(weaponAddress: Long, index: Int, active: Boolean): Int {
		val weaponId = process().readInt(weaponAddress + m_iItemDefinitionIndex)
		if (weaponId == Weapons.C4.id) {
			isBombCarrier = true
			
		}
		weaponIds[index][0] = weaponId.toLong()
		weaponIds[index][1] = weaponAddress
		return weaponId
	}
	
}
