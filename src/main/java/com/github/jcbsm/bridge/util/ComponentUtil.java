package com.github.jcbsm.bridge.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentUtil {

    public static String getPlainText(Component component) {

        return PlainTextComponentSerializer.plainText().serialize(component);

    }
}
