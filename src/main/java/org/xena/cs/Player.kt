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

import org.xena.offsets.OffsetManager.clientModule
import org.xena.offsets.OffsetManager.process
import org.xena.offsets.offsets.ClientOffsets.dwEntityList
import org.xena.offsets.offsets.NetVarOffsets.hMyWeapons
import org.xena.offsets.offsets.NetVarOffsets.iItemDefinitionIndex

open class Player : GameEntity() {
	
	var weaponIds = Array(8) { LongArray(2) }

	var name = "hz"
	
	override fun update() {
		//val id = process().readUnsignedInt(address() + 0x64)
		//val radarBase = process().readUnsignedInt(clientModule().address() + 0x04EC039C)
		//val radar = process().readUnsignedInt(radarBase + 0x54)
		//name = process().readString(radar + ((0x1E0 * (id+1)) + 0x24), 40)
		super.update()

		isBombCarrier = false

		weaponIds = Array(8) { LongArray(2) }

		for (i in weaponIds.indices) {
			val currentWeaponIndex = process().readUnsignedInt(address() + hMyWeapons.toLong() + ((i - 1) * 0x04).toLong()) and 0xFFF
			val weaponAddress = clientModule().readUnsignedInt(dwEntityList + (currentWeaponIndex - 1) * 0x10)
			
			if (weaponAddress > 0) {
				processWeapon(weaponAddress, i, false)
			}
		}
	}
	
	open fun processWeapon(weaponAddress: Long, index: Int, active: Boolean): Int {
		val weaponId = process().readShort(weaponAddress + iItemDefinitionIndex)
		if (weaponId == Weapons.C4.id) {
			isBombCarrier = true
			
		}
		weaponIds[index][0] = weaponId.toLong()
		weaponIds[index][1] = weaponAddress
		return weaponId
	}
	
}
