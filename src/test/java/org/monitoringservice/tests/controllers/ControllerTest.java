package org.monitoringservice.tests.controllers;

import org.monitoringservice.entities.Role;
import org.springframework.mock.web.MockHttpSession;

import java.util.LinkedList;

public interface ControllerTest {
    default MockHttpSession getTestSessionForUser(){
        MockHttpSession testSession = new MockHttpSession();
        testSession.setAttribute("id", 12);
        testSession.setAttribute("role", Role.USER);
        return testSession;
    }

    default MockHttpSession getTestSessionForAdmin(){
        MockHttpSession testSession = new MockHttpSession();
        testSession.setAttribute("id", 1);
        testSession.setAttribute("role", Role.ADMIN);
        return testSession;
    }

    default LinkedList<String> getTestList(){
        LinkedList<String> testList = new LinkedList<>();
        testList.add("firstResult");
        testList.add("secondResult");
        return testList;
    }
}
