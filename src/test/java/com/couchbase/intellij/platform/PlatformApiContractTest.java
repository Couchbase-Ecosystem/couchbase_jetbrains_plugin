package com.couchbase.intellij.platform;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Pins the IntelliJ Platform APIs used where the plugin builds a DataContext to invoke an action programmatically
 * (ListStackImpl#handleDoubleClick), after moving off the Map-as-DataContext trick marked for removal.
 */
public class PlatformApiContractTest extends BasePlatformTestCase {

    public void testSimpleDataContextCarriesSelectedItemAndProject() {
        Project project = getProject();
        Object selected = new Object();

        DataContext ctx = SimpleDataContext.builder()
                .add(PlatformDataKeys.SELECTED_ITEM, selected)
                .add(CommonDataKeys.PROJECT, project)
                .build();

        assertSame(selected, ctx.getData(PlatformDataKeys.SELECTED_ITEM));
        assertSame(project, ctx.getData(CommonDataKeys.PROJECT));
        assertNull(ctx.getData(CommonDataKeys.EDITOR));
    }

    public void testActionEventCanBeBuiltFromADataContextAndExposesItsData() {
        Object selected = new Object();
        DataContext ctx = SimpleDataContext.builder().add(PlatformDataKeys.SELECTED_ITEM, selected).build();

        AnActionEvent event = AnActionEvent.createEvent(ctx, new Presentation(), "ChatGPT", ActionUiKind.NONE, null);

        assertSame(selected, event.getData(PlatformDataKeys.SELECTED_ITEM));
        assertSame(ctx, event.getDataContext());
    }
}
