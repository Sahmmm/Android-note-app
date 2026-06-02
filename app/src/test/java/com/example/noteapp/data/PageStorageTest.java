package com.example.noteapp.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.noteapp.model.Page;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

public class PageStorageTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void loadPagesReturnsEmptyListWhenFileDoesNotExist() throws Exception {
        PageStorage storage = new PageStorage(temporaryFolder.getRoot());

        List<Page> pages = storage.loadPages(false);

        assertTrue(pages.isEmpty());
        assertFalse(storage.hasPagesFile(false));
    }

    @Test
    public void addPageWritesPageToClassicFile() throws Exception {
        PageStorage storage = new PageStorage(temporaryFolder.getRoot());
        Page page = new Page("page-1", "Title", "N", "#FFFFFF", false);

        storage.addPage(page);

        List<Page> pages = storage.loadPages(false);
        assertEquals(1, pages.size());
        assertEquals("page-1", pages.get(0).getId());
        assertEquals("Title", pages.get(0).getTitle());
        assertEquals("N", pages.get(0).getIcon());
        assertTrue(new File(temporaryFolder.getRoot(), PageStorage.PAGES_FILE).exists());
        assertFalse(new File(temporaryFolder.getRoot(), PageStorage.SECRET_PAGES_FILE).exists());
    }

    @Test
    public void addSecretPageWritesPageToSecretFile() throws Exception {
        PageStorage storage = new PageStorage(temporaryFolder.getRoot());
        Page page = new Page("secret-1", "Secret", "S", "#000000", true);

        storage.addPage(page);

        List<Page> pages = storage.loadPages(true);
        assertEquals(1, pages.size());
        assertEquals("secret-1", pages.get(0).getId());
        assertTrue(new File(temporaryFolder.getRoot(), PageStorage.SECRET_PAGES_FILE).exists());
        assertFalse(new File(temporaryFolder.getRoot(), PageStorage.PAGES_FILE).exists());
    }

    @Test
    public void updatePageChangesContentTitleAndIcon() throws Exception {
        PageStorage storage = new PageStorage(temporaryFolder.getRoot());
        Page page = new Page("page-1", "Title", "old content", "N", "#FFFFFF", "#FFFFFFFF", false);
        storage.addPage(page);

        Page updatedPage = new Page("page-1", "Updated title", "U", "#FFFFFF", false);
        storage.updatePage(updatedPage, "updated content");

        List<Page> pages = storage.loadPages(false);
        assertEquals(1, pages.size());
        assertEquals("Updated title", pages.get(0).getTitle());
        assertEquals("updated content", pages.get(0).getContent());
        assertEquals("U", pages.get(0).getIcon());
    }

    @Test
    public void updatePageKeepsExistingIconWhenNewIconIsEmpty() throws Exception {
        PageStorage storage = new PageStorage(temporaryFolder.getRoot());
        Page page = new Page("page-1", "Title", "old content", "N", "#FFFFFF", "#FFFFFFFF", false);
        storage.addPage(page);

        Page updatedPage = new Page("page-1", "Updated title", "", "#FFFFFF", false);
        updatedPage.setIcon("");
        storage.updatePage(updatedPage, "updated content");

        List<Page> pages = storage.loadPages(false);
        assertEquals(1, pages.size());
        assertEquals("Updated title", pages.get(0).getTitle());
        assertEquals("N", pages.get(0).getIcon());
    }

    @Test
    public void deletePageRemovesMatchingPageOnly() throws Exception {
        PageStorage storage = new PageStorage(temporaryFolder.getRoot());
        Page firstPage = new Page("page-1", "First", "F", "#FFFFFF", false);
        Page secondPage = new Page("page-2", "Second", "S", "#FFFFFF", false);
        storage.addPage(firstPage);
        storage.addPage(secondPage);

        storage.deletePage(firstPage);

        List<Page> pages = storage.loadPages(false);
        assertEquals(1, pages.size());
        assertEquals("page-2", pages.get(0).getId());
    }
}
