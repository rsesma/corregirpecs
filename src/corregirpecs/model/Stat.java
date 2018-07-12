package corregirpecs.model;

import java.util.ArrayList;

public class Stat {
	public ArrayList<Item> items = new ArrayList<Item>();
	
	public Stat(ArrayList<Pregunta> plantilla) {
		for (Pregunta p: plantilla) {
			this.items.add(new Item(p));
		}
	}
	
	public Item getItem(String p) {
		for (Item i: this.items) {
			if (i.pregunta.equals(p)) return i;
		}
		return null;
	}
}
