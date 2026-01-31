package funny.leet.api.system.font.entry;

import funny.leet.api.system.font.glyph.Glyph;

public record DrawEntry(float atX, float atY, int color, Glyph toDraw) {
}
