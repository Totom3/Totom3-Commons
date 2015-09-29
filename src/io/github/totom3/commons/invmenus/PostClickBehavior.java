package io.github.totom3.commons.invmenus;

/**
 *
 * @author Totom3
 */
public interface PostClickBehavior {

    public static final PostClickBehavior NONE = (m) -> {
    };

    public static final PostClickBehavior CLOSE = (m) -> m.close();

    public static final PostClickBehavior OPEN_PARENT = (m) -> m.openParent();

    void apply(InventoryMenu menu);

}
