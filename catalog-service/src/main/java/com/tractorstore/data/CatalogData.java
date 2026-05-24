package com.tractorstore.data;

import com.tractorstore.model.Category;
import com.tractorstore.model.Product;
import com.tractorstore.model.Store;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * In-memory product catalog. Colors are stored as hex strings (e.g. "#FF0000")
 * so the color-distance algorithm can parse them into RGB components.
 */
@Component
public class CatalogData {

    public List<Product> getAllProducts() {
        return List.of(
            new Product("1",  "TRK-001", "Tractor Rojo Clásico",        12500.00, "https://placehold.co/400x300/CC0000/white?text=TRK-001", "classic",   "#CC0000", "Diesel 75HP"),
            new Product("2",  "TRK-002", "Tractor Azul Clásico",        11800.00, "https://placehold.co/400x300/0055CC/white?text=TRK-002", "classic",   "#0055CC", "Diesel 70HP"),
            new Product("3",  "TRK-003", "Tractor Verde Clásico",       13200.00, "https://placehold.co/400x300/228B22/white?text=TRK-003", "classic",   "#228B22", "Diesel 80HP"),
            new Product("4",  "TRK-004", "Tractor Amarillo Clásico",    10900.00, "https://placehold.co/400x300/FFD700/black?text=TRK-004", "classic",   "#FFD700", "Gasolina 65HP"),
            new Product("5",  "TRK-005", "Tractor Naranja Clásico",     11200.00, "https://placehold.co/400x300/FF6600/white?text=TRK-005", "classic",   "#FF6600", "Gasolina 68HP"),
            new Product("6",  "TRK-006", "Tractor Blanco Clásico",      14500.00, "https://placehold.co/400x300/F0F0F0/black?text=TRK-006", "classic",   "#F0F0F0", "Diesel 90HP"),
            new Product("7",  "TRK-007", "Tractor Negro Clásico",       15000.00, "https://placehold.co/400x300/222222/white?text=TRK-007", "classic",   "#222222", "Diesel 95HP"),
            new Product("8",  "TRK-008", "Tractor Gris Clásico",        11500.00, "https://placehold.co/400x300/888888/white?text=TRK-008", "classic",   "#888888", "Gasolina 70HP"),
            new Product("9",  "AUT-001", "Tractor Autónomo Rojo",       28000.00, "https://placehold.co/400x300/FF3333/white?text=AUT-001", "autonomous","#FF3333", "Eléctrico 100HP"),
            new Product("10", "AUT-002", "Tractor Autónomo Azul",       26500.00, "https://placehold.co/400x300/3366FF/white?text=AUT-002", "autonomous","#3366FF", "Eléctrico 95HP"),
            new Product("11", "AUT-003", "Tractor Autónomo Verde",      27800.00, "https://placehold.co/400x300/33AA33/white?text=AUT-003", "autonomous","#33AA33", "Eléctrico 98HP"),
            new Product("12", "AUT-004", "Tractor Autónomo Amarillo",   25000.00, "https://placehold.co/400x300/FFEE00/black?text=AUT-004", "autonomous","#FFEE00", "Eléctrico 90HP"),
            new Product("13", "AUT-005", "Tractor Autónomo Naranja",    25500.00, "https://placehold.co/400x300/FF8800/white?text=AUT-005", "autonomous","#FF8800", "Eléctrico 88HP"),
            new Product("14", "AUT-006", "Tractor Autónomo Blanco",     30000.00, "https://placehold.co/400x300/FFFFFF/black?text=AUT-006", "autonomous","#FFFFFF", "Eléctrico 110HP"),
            new Product("15", "AUT-007", "Tractor Autónomo Negro",      31000.00, "https://placehold.co/400x300/111111/white?text=AUT-007", "autonomous","#111111", "Eléctrico 115HP"),
            new Product("16", "AUT-008", "Tractor Autónomo Gris",       24500.00, "https://placehold.co/400x300/999999/white?text=AUT-008", "autonomous","#999999", "Eléctrico 85HP"),
            new Product("17", "TRK-009", "Tractor Borgoña Clásico",     12800.00, "https://placehold.co/400x300/800020/white?text=TRK-009", "classic",   "#800020", "Diesel 78HP"),
            new Product("18", "TRK-010", "Tractor Azul Marino Clásico", 13000.00, "https://placehold.co/400x300/001F5B/white?text=TRK-010", "classic",   "#001F5B", "Diesel 82HP"),
            new Product("19", "AUT-009", "Tractor Autónomo Turquesa",   27000.00, "https://placehold.co/400x300/00CED1/black?text=AUT-009", "autonomous","#00CED1", "Eléctrico 92HP"),
            new Product("20", "AUT-010", "Tractor Autónomo Morado",     28500.00, "https://placehold.co/400x300/7B2FBE/white?text=AUT-010", "autonomous","#7B2FBE", "Eléctrico 105HP")
        );
    }

    public List<Category> getAllCategories() {
        return List.of(
            new Category("cat-1", "Tractores Clásicos",   "classic",   "https://placehold.co/600x400/228B22/white?text=Clasicos",   "Potencia y tradición en cada surco."),
            new Category("cat-2", "Tractores Autónomos",  "autonomous","https://placehold.co/600x400/0055CC/white?text=Autonomos",  "Tecnología de punta para el campo del futuro.")
        );
    }

    public List<Store> getAllStores() {
        return List.of(
            new Store("s1", "Tractor Store Bogotá",       "Av. El Dorado 92-48",       "Bogotá",      "+57 1 234 5678",  "bogota@tractorstore.co",     4.6534,  -74.0833, "Lun-Sáb 8:00-18:00"),
            new Store("s2", "Tractor Store Medellín",     "Calle 10 # 43-18",          "Medellín",    "+57 4 345 6789",  "medellin@tractorstore.co",   6.2518,  -75.5636, "Lun-Sáb 8:00-17:00"),
            new Store("s3", "Tractor Store Cali",         "Carrera 5 # 12-35",         "Cali",        "+57 2 456 7890",  "cali@tractorstore.co",       3.4516,  -76.5320, "Lun-Vie 8:00-17:00"),
            new Store("s4", "Tractor Store Barranquilla", "Calle 77 # 50-28",          "Barranquilla","+57 5 567 8901",  "bquilla@tractorstore.co",   10.9639, -74.7964, "Lun-Sáb 8:00-17:00"),
            new Store("s5", "Tractor Store Cartagena",    "Av. Pedro de Heredia # 23", "Cartagena",   "+57 5 678 9012",  "cartagena@tractorstore.co", 10.3997, -75.5144, "Lun-Sáb 8:00-18:00")
        );
    }
}
