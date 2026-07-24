package com.segnities007.stylishui.foundation

/**
 * A component is actionable only when it is enabled and owns a real action.
 *
 * Callers should represent a missing action with `null`, never with an empty lambda.
 */
fun isActionable(
    enabled: Boolean = true,
    hasClickAction: Boolean,
    hasLongClickAction: Boolean = false,
): Boolean = enabled && (hasClickAction || hasLongClickAction)
