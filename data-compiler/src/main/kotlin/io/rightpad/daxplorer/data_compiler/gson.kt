package io.rightpad.daxplorer.data_compiler

import com.google.gson.*
import io.rightpad.daxplorer.data.features.AverageFeatureConfig
import io.rightpad.daxplorer.data.features.FeatureConfig
import io.rightpad.daxplorer.data.features.RelativeStrengthIndexFeatureConfig
import java.lang.reflect.Type

val GSON = GsonBuilder()
        .registerTypeAdapter(FeatureConfig::class.java, FeatureConfigDeserializer())
        .registerTypeAdapter(FeatureConfig::class.java, FeatureConfigSerializer())
        .create()

class FeatureConfigDeserializer: JsonDeserializer<FeatureConfig<*, *>> {
    override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
    ): FeatureConfig<*, *> {
        val type = json!!.asJsonObject.get("type").asString
        return when(type) {
            "average"               -> context!!.deserialize<AverageFeatureConfig>(json, AverageFeatureConfig::class.java)
            "rsi" -> context!!.deserialize<RelativeStrengthIndexFeatureConfig>(json, RelativeStrengthIndexFeatureConfig::class.java)
            else                    -> throw IllegalArgumentException("type property of FeatureConfig has illegal value: $type")
        }
    }
}

class FeatureConfigSerializer: JsonSerializer<FeatureConfig<*, *>> {
    override fun serialize(src: FeatureConfig<*, *>?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val serialized = context!!.serialize(src)

        val type = when(src) {
            is AverageFeatureConfig               -> "average"
            is RelativeStrengthIndexFeatureConfig -> "rsi"
            else                                  -> throw IllegalArgumentException(
                    if(src == null) "FeatureConfig is null"
                    else "FeatureConfig has unknown type: ${src::class.qualifiedName}"
            )
        }
        serialized.asJsonObject.addProperty("type", type)

        return serialized
    }
}
