package com.example.noteapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    @Test
    public void pageCanRoundTripThroughJacksonLikeRepositoryPersistence() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Page savedPage = new Page("id", "Titre", "", "#B0E1FA", false, Page.TYPE_NOTE, "", "");

        String json = objectMapper.writeValueAsString(Collections.singletonList(savedPage));
        List<Page> loadedPages = objectMapper.readValue(json, new TypeReference<List<Page>>() {});

        assertFalse(json.contains("\"typeLabel\""));
        assertFalse(json.contains("\"reminderLabel\""));
        assertFalse(json.contains("\"reminder\""));
        assertFalse(json.contains("\"list\""));
        assertEquals(1, loadedPages.size());
        assertEquals("id", loadedPages.get(0).getId());
        assertEquals("Titre", loadedPages.get(0).getTitle());
        assertEquals(Page.TYPE_NOTE, loadedPages.get(0).getType());
    }

    @Test
    public void pageCanLoadJsonThatContainsPreviouslySerializedDisplayFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "[{\"id\":\"id\",\"title\":\"Titre\",\"content\":\"\",\"icon\":\"\",\"colorFont\":\"#B0E1FA\","
                + "\"colorText\":\"#FFFFFFFF\",\"time\":\"12:00\",\"date\":\"05/06/2026\",\"type\":\"note\","
                + "\"reminderDate\":\"\",\"reminderTime\":\"\",\"isSecret\":false,\"typeLabel\":\"Note\","
                + "\"reminderLabel\":\"\",\"reminder\":false,\"list\":false}]";

        List<Page> loadedPages = objectMapper.readValue(json, new TypeReference<List<Page>>() {});

        assertEquals(1, loadedPages.size());
        assertEquals("id", loadedPages.get(0).getId());
        assertEquals("Titre", loadedPages.get(0).getTitle());
        assertFalse(loadedPages.get(0).getIsSecret());
    }
}
