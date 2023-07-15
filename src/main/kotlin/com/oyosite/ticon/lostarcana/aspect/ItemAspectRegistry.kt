package com.oyosite.ticon.lostarcana.aspect

import com.oyosite.ticon.lostarcana.LostArcana
import com.oyosite.ticon.lostarcana.item.VisCrystalItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

object ItemAspectRegistry: Map<Identifier, List<Pair<Aspect,Int>>> {
    private val ASPECTS = mutableMapOf<String, List<Pair<Aspect,Int>>>()
    override fun get(key: Identifier): List<Pair<Aspect,Int>>? = ASPECTS[key.toString()]
    operator fun get(item: Item): List<Pair<Aspect,Int>>? = this[Registries.ITEM.getId(item)]
    operator fun get(stack: ItemStack): List<Pair<Aspect,Int>>? {
        val otpt = mutableListOf<Pair<Aspect,Int>>()
        if(stack.item is VisCrystalItem) (stack.item as VisCrystalItem).getAspect(stack)?.let{otpt.add(it to 810)}
        this[stack.item]?.also(otpt::addAll)
        return if(otpt.isEmpty()) null else otpt
    }
    override val entries: Set<Map.Entry<Identifier, List<Pair<Aspect,Int>>>>
        get() = ASPECTS.mapKeys{LostArcana.id(it.key)}.entries
    override val keys: Set<Identifier>
        get() = ASPECTS.keys.map(LostArcana::id).toSet()
    override val size: Int
        get() = ASPECTS.size
    override val values: Collection<List<Pair<Aspect,Int>>>
        get() = ASPECTS.values

    override fun isEmpty(): Boolean = ASPECTS.isEmpty()

    override fun containsValue(value: List<Pair<Aspect,Int>>): Boolean = ASPECTS.containsValue(value)

    override fun containsKey(key: Identifier): Boolean = ASPECTS.containsKey(key.toString())


}