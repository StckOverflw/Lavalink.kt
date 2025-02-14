package dev.schlaubi.lavakord.audio.player

import dev.schlaubi.lavakord.audio.internal.GatewayPayload
import dev.schlaubi.lavakord.checkImplementation
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Representation of the filter configuration.
 */
@Suppress("KDocMissingDocumentation", "KDocMissingDocumentation") // I don't know anything about music
public interface Filters : EqualizerBuilder {
    public var volume: Float?
    public val karaoke: Karaoke?
    public val timescale: Timescale?
    public val tremolo: Tremolo?
    public val vibrato: Vibrato?
    public val rotation: Rotation?
    public val distortion: Distortion?
    public val channelMix: ChannelMix?
    public val lowPass: LowPass?

    /**
     * Root interface for a resettable filter.
     */
    public interface Filter {
        /**
         * Resets this filter to it's default state.
         */
        public fun reset()
    }

    public override fun reset() {
        require(this is GatewayPayload.FiltersCommand)
        super.reset()
        karaoke = null
        timescale = null
        tremolo = null
        vibrato = null
    }

    /**
     * Uses equalization to eliminate part of a band, usually targeting vocals.
     */
    public interface Karaoke : Filter {
        public var level: Float
        public var monoLevel: Float
        public var filterBand: Float
        public var filterWidth: Float
    }

    /**
     *  Changes the speed, pitch, and rate. All default to 1.
     *
     * @property speed must be greater than 0
     * @property pitch must be greater than 0
     * @property rate must be greater than 0
     */
    public interface Timescale : Filter {
        public var speed: Float
        public var pitch: Float
        public var rate: Float
    }

    /**
     * Uses amplification to create a shuddering effect, where the volume quickly oscillates.
     * Example: https://en.wikipedia.org/wiki/File:Fuse_Electronics_Tremolo_MK-III_Quick_Demo.ogv
     * @property frequency must be greater than 0
     * @property depth must be between 0 and 1
     */
    public interface Tremolo : Filter {
        public var frequency: Float
        public var depth: Float
    }

    /**
     * Similar to [Tremolo]. While tremolo oscillates the volume, vibrato oscillates the pitch.
     */
    public interface Vibrato : Tremolo

    /**
     * Rotates the sound around the stereo channels/user headphones aka Audio Panning. It can produce an effect similar to: https://youtu.be/QB9EB8mTKcc (without the reverb)
     *
     * @property rotationHz The frequency of the audio rotating around the listener in Hz. 0.2 is similar to the example video above.
     */
    public interface Rotation {
        public var rotationHz: Float
    }

    /**
     * Distortion effect. It can generate some pretty unique audio effects.
     */
    public interface Distortion {
        public var sinOffset: Float
        public var sinScale: Float
        public var cosOffset: Float
        public var cosScale: Float
        public var tanOffset: Float
        public var tanScale: Float
        public var offset: Float
        public var scal: Float
    }

    /**
     * Mixes both channels (left and right), with a configurable factor on how much each channel affects the other.
     * With the defaults, both channels are kept independent from each other.
     * Setting all factors to 0.5 means both channels get the same audio.
     */
    public interface ChannelMix {
        public var leftToLeft: Float
        public var leftToRight: Float
        public var rightToLeft: Float
        public var rightToRight: Float
    }

    /**
     * Higher frequencies get suppressed, while lower frequencies pass through this filter, thus the name low pass.
     */
    public interface LowPass {
        public var smoothing: Float
    }
}


/**
 * Resets all applied filters.
 */
public suspend fun Player.resetFilters() {
    applyFilters { reset() }
}

/**
 * Applies all Filters to this player.
 */
@OptIn(ExperimentalContracts::class)
public suspend fun Player.applyFilters(block: Filters.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    checkImplementation()
    val filters = filters
    filters.apply(block)

    node.send(filters)
}

/**
 * Configures the [Filters.Karaoke] filter.
 */
@OptIn(ExperimentalContracts::class)
public fun Filters.karaoke(block: Filters.Karaoke.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    checkImplementation()
    if (karaoke == null) {
        karaoke = GatewayPayload.FiltersCommand.Karaoke()
    }
    val filter = karaoke ?: return

    filter.apply(block)
}

/**
 * Configures the [Filters.Timescale] filter.
 */
@OptIn(ExperimentalContracts::class)
public fun Filters.timescale(block: Filters.Timescale.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    checkImplementation()
    if (timescale == null) {
        timescale = GatewayPayload.FiltersCommand.Timescale()
    }
    val filter = timescale ?: return

    filter.apply(block)
}

/**
 * Configures the [Filters.Tremolo] filter.
 */
@OptIn(ExperimentalContracts::class)
public fun Filters.tremolo(block: Filters.Tremolo.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    checkImplementation()
    if (tremolo == null) {
        tremolo = GatewayPayload.FiltersCommand.Tremolo()
    }
    val filter = tremolo ?: return

    filter.apply(block)
}

/**
 * Configures the [Filters.Vibrato] filter.
 */
@OptIn(ExperimentalContracts::class)
public fun Filters.vibrato(block: Filters.Vibrato.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    checkImplementation()
    if (vibrato == null) {
        vibrato = GatewayPayload.FiltersCommand.Vibrato()
    }
    val filter = vibrato ?: return

    filter.apply(block)
}

/**
 * Configures the [Filters.Rotation] filter.
 */
@OptIn(ExperimentalContracts::class)
public fun Filters.rotation(block: Filters.Rotation.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    checkImplementation()
    if (rotation == null) {
        rotation = GatewayPayload.FiltersCommand.Rotation()
    }
    val filter = rotation ?: return

    filter.apply(block)
}

/**
 * Configures the [Filters.Distortion] filter.
 */
@OptIn(ExperimentalContracts::class)
public fun Filters.distortion(block: Filters.Distortion.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    checkImplementation()
    if (distortion == null) {
        distortion = GatewayPayload.FiltersCommand.Distortion()
    }
    val filter = distortion ?: return

    filter.apply(block)
}

/**
 * Configures the [Filters.ChannelMix] filter.
 */
@OptIn(ExperimentalContracts::class)
public fun Filters.channelMix(block: Filters.ChannelMix.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    checkImplementation()
    if (channelMix == null) {
        channelMix = GatewayPayload.FiltersCommand.ChannelMix()
    }
    val filter = channelMix ?: return

    filter.apply(block)
}

/**
 * Configures the [Filters.LowPass] filter.
 */
@OptIn(ExperimentalContracts::class)
public fun Filters.lowPass(block: Filters.LowPass.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    checkImplementation()
    if (lowPass == null) {
        lowPass = GatewayPayload.FiltersCommand.LowPass()
    }
    val filter = lowPass ?: return

    filter.apply(block)
}

@OptIn(ExperimentalContracts::class)
private fun Filters.checkImplementation() {
    contract {
        returns() implies (this@checkImplementation is GatewayPayload.FiltersCommand)
    }
    require(this is GatewayPayload.FiltersCommand) { "This has to be a internal implementation instance" }
}
