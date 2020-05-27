/**
 * Rodrigo Maafs | AppsCamelot 2020
 */

package timer.avances;

/**
 *
 * @author Rodrigo Maafs
 */
public class RenglonXLS {
    public String ambiente, link;
    public int numero;
    public double tiempo;

    public RenglonXLS(String ambiente, int numero, String link, double hrs) {
        this.ambiente = ambiente;
        this.numero = numero;
        this.link = link;
        this.tiempo = hrs;
    }
}
