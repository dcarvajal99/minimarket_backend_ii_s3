package com.minimarket.security.xss;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * Utilidad de sanitizacion contra Cross-Site Scripting (XSS).
 *
 * <p>Emplea jsoup con una politica {@link Safelist#none()} (no se permite
 * ninguna etiqueta HTML), de modo que cualquier marcado o script incrustado en
 * las entradas del usuario es eliminado o neutralizado antes de almacenarse.
 * Asi, un valor como {@code <script>alert('x')</script>} se convierte en texto
 * inofensivo.
 */
public final class XssSanitizer {

    private XssSanitizer() {
    }

    /**
     * Limpia una cadena eliminando todo el HTML/JS y dejando solo texto plano.
     *
     * @param input texto potencialmente malicioso
     * @return texto saneado, o {@code null} si la entrada era {@code null}
     */
    public static String clean(String input) {
        if (input == null) {
            return null;
        }
        // Safelist.none() => sin etiquetas permitidas. preserveRelativeLinks=false.
        // Se desactiva el escape de entidades para devolver texto legible.
        String cleaned = Jsoup.clean(input, Safelist.none());
        // Jsoup escapa caracteres como & < >; los revertimos a su forma textual
        // ya que el contenido peligroso (etiquetas) ya fue removido.
        return Jsoup.parse(cleaned).text();
    }
}
