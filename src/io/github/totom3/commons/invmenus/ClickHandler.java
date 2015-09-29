package io.github.totom3.commons.invmenus;

/**
 *
 * @author Totom3
 */
public interface ClickHandler {

    ClickHandler NONE = e -> {
    };

    ClickHandler CLOSE = e -> e.setBehavior(PostClickBehavior.CLOSE);

    ClickHandler OPEN_PARENT = e -> e.setBehavior(PostClickBehavior.OPEN_PARENT);

    ClickHandler TRY_OPEN_PARENT = e -> {
	e.setBehavior((e.getMenu().hasParent()) ? PostClickBehavior.OPEN_PARENT : PostClickBehavior.CLOSE);
    };

    void onClick(MenuClickEvent event);

}
