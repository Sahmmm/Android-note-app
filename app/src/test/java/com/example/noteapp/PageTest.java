package com.example.noteapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PageTest {
    @Test
    public void pageDefaultsToNoteWhenTypeIsMissing() {
        Page page = new Page();

        assertEquals(Page.TYPE_NOTE, page.getType());
        assertEquals("Note", page.getTypeLabel());
        assertEquals("\uD83D\uDCC4", page.getIcon());
        assertEquals("#B0E1FA", page.getColorFont());
    }

    @Test
    public void reminderKeepsScheduleLabel() {
        Page page = new Page("id", "Titre", "", "#B0E1FA", false, Page.TYPE_REMINDER, "02/06/2026", "09:30");

        assertTrue(page.isReminder());
        assertEquals("Rappel", page.getTypeLabel());
        assertEquals("02/06/2026 09:30", page.getReminderLabel());
    }

    @Test
    public void listTypeIsRecognized() {
        Page page = new Page("id", "Courses", "", "#B0E1FA", false, Page.TYPE_LIST, "", "");

        assertTrue(page.isList());
        assertEquals("Liste", page.getTypeLabel());
    }
}
