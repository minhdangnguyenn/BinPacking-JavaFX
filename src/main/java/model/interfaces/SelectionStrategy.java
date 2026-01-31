package model.interfaces;

import java.util.ArrayList;

public abstract class SelectionStrategy<I> {

    public abstract ArrayList<I> orderItems(ArrayList<I> items);
}
