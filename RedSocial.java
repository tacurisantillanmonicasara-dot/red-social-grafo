import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Red social "ConectaAmbato": grafo simple, no dirigido y no ponderado.
 * 6 usuarios (vertices) y 5 amistades (aristas).
 *
 * Condiciones del reto que se verifican en el programa:
 *  - Exactamente 6 usuarios con id y nombre distintos.
 *  - Exactamente 5 amistades, sin lazos ni duplicados.
 *  - Al menos un ciclo de 3 o mas vertices.
 *  - Al menos un usuario aislado (grado 0).
 *  - Al menos un usuario con grado 3 o mayor.
 */
public class RedSocial {

    // id -> nombre (LinkedHashMap conserva el orden de insercion)
    private final Map<String, String> usuarios = new LinkedHashMap<>();
    // id -> ids de sus amigos (lista de adyacencia)
    private final Map<String, List<String>> amigos = new LinkedHashMap<>();
    private int totalAmistades = 0;

    /** Agrega un usuario si su id y su nombre no existen todavia. */
    public boolean agregarUsuario(String id, String nombre) {
        if (usuarios.containsKey(id) || usuarios.containsValue(nombre)) {
            return false;
        }
        usuarios.put(id, nombre);
        amigos.put(id, new ArrayList<>());
        return true;
    }

    /** Agrega una amistad mutua. Rechaza lazos, usuarios inexistentes y duplicados. */
    public boolean agregarAmistad(String a, String b) {
        if (a.equals(b)) return false;                                   // sin lazos
        if (!usuarios.containsKey(a) || !usuarios.containsKey(b)) return false;
        if (amigos.get(a).contains(b)) return false;                     // sin duplicados
        amigos.get(a).add(b);
        amigos.get(b).add(a);                                            // no dirigido
        totalAmistades++;
        return true;
    }

    public int grado(String id) {
        return amigos.get(id).size();
    }

    /** Busca un ciclo de 3 o mas vertices con DFS (devuelve true si existe). */
    public boolean tieneCiclo() {
        List<String> visitados = new ArrayList<>();
        for (String id : usuarios.keySet()) {
            if (!visitados.contains(id) && dfsCiclo(id, null, visitados)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfsCiclo(String actual, String padre, List<String> visitados) {
        visitados.add(actual);
        for (String vecino : amigos.get(actual)) {
            if (!visitados.contains(vecino)) {
                if (dfsCiclo(vecino, actual, visitados)) return true;
            } else if (!vecino.equals(padre)) {
                return true; // vecino ya visitado que no es el padre => ciclo
            }
        }
        return false;
    }

    public void mostrar() {
        System.out.println("=== RED SOCIAL: ConectaAmbato ===");
        System.out.println("Usuarios (vertices): " + usuarios.size());
        System.out.println("Amistades (aristas): " + totalAmistades);
        System.out.println();
        System.out.println("Lista de adyacencia:");
        for (String id : usuarios.keySet()) {
            StringBuilder sb = new StringBuilder();
            for (String amigo : amigos.get(id)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(amigo).append(" (").append(usuarios.get(amigo)).append(")");
            }
            System.out.printf("  %s %-8s | grado %d | amigos: %s%n",
                    id, usuarios.get(id), grado(id),
                    sb.length() == 0 ? "(ninguno)" : sb.toString());
        }
    }

    public void verificarCondiciones() {
        boolean seisUsuarios = usuarios.size() == 6;
        boolean cincoAmistades = totalAmistades == 5;
        boolean hayCiclo = tieneCiclo();
        boolean hayAislado = false;
        boolean hayGrado3 = false;
        int sumaGrados = 0;
        for (String id : usuarios.keySet()) {
            int g = grado(id);
            sumaGrados += g;
            if (g == 0) hayAislado = true;
            if (g >= 3) hayGrado3 = true;
        }
        boolean lemaApreton = sumaGrados == 2 * totalAmistades;

        System.out.println();
        System.out.println("Verificacion de condiciones:");
        System.out.println("  [" + marca(seisUsuarios) + "] Exactamente 6 usuarios");
        System.out.println("  [" + marca(cincoAmistades) + "] Exactamente 5 amistades");
        System.out.println("  [" + marca(hayCiclo) + "] Al menos un ciclo de 3 o mas vertices");
        System.out.println("  [" + marca(hayAislado) + "] Al menos un usuario aislado (grado 0)");
        System.out.println("  [" + marca(hayGrado3) + "] Al menos un usuario con grado >= 3");
        System.out.println("  [" + marca(lemaApreton) + "] Suma de grados (" + sumaGrados
                + ") = 2 x aristas (" + 2 * totalAmistades + ")");
    }

    private static String marca(boolean ok) {
        return ok ? "OK" : "FALLA";
    }

    public static void main(String[] args) {
        RedSocial red = new RedSocial();

        // 6 usuarios
        red.agregarUsuario("U01", "Mateo");
        red.agregarUsuario("U02", "Camila");
        red.agregarUsuario("U03", "Sofia");
        red.agregarUsuario("U04", "Andres");
        red.agregarUsuario("U05", "Valeria");
        red.agregarUsuario("U06", "Joaquin");

        // 5 amistades
        red.agregarAmistad("U01", "U02");
        red.agregarAmistad("U02", "U03");
        red.agregarAmistad("U03", "U01"); // cierra el triangulo U01-U02-U03
        red.agregarAmistad("U01", "U04");
        red.agregarAmistad("U04", "U05");

        // Pruebas de que el grafo es simple (deben ser rechazadas)
        System.out.println("Lazo U01-U01 rechazado?      " + !red.agregarAmistad("U01", "U01"));
        System.out.println("Duplicado U02-U01 rechazado? " + !red.agregarAmistad("U02", "U01"));
        System.out.println();

        red.mostrar();
        red.verificarCondiciones();
    }
}
