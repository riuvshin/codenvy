/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.cli.command.builtin;

import com.codenvy.client.Codenvy;
import com.codenvy.client.CodenvyAPI;
import com.codenvy.client.CodenvyClient;
import com.codenvy.client.model.Project;
import com.codenvy.client.model.Workspace;

import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.fusesource.jansi.Ansi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import static com.codenvy.cli.command.builtin.Constants.CODENVY_CONFIG_FILE;

/**
 * Abstract command which should be extended by all Codenvy commands.
 * @author Florent Benoit
 */
public abstract class AbsCommand extends OsgiCommandSupport {

    /**
     * Codenvy settings read from its configuration file.
     */
    private Properties codenvySettings;

    /**
     * Codenvy client instance.
     */
    private CodenvyClient codenvyClient;

    /**
     * Get a configuration property from the Codenvy configuration file stored in KARAF_HOME/etc folder.
     * @param property the name of the property
     * @return the value or null if not found
     */
    protected String getCodenvyProperty(String property) {
        // Load the settings if not yet loaded
        if (codenvySettings == null) {
            // load the codenvy setting
            try (Reader reader = new InputStreamReader(new FileInputStream(CODENVY_CONFIG_FILE), Charset.defaultCharset())) {
                codenvySettings = new Properties();
                codenvySettings.load(reader);
            } catch (IOException e) {
                session.getConsole().println("Unable to load condenvy settings" + e.getMessage());
                throw new IllegalStateException("Unable to load codenvy settings", e);
            }
        }
        return codenvySettings.getProperty(property);
    }

    /**
     * @return the current Codenvy instance used at runtime
     */
    protected Codenvy getCurrentCodenvy() {
        return (Codenvy)session.get(Codenvy.class.getName());
    }

    /**
     * @return the current workspace used at runtime
     */
    protected CodenvyClient getCodenvyClient() {
        if (codenvyClient == null) {
            codenvyClient = CodenvyAPI.getClient();
        }
        return codenvyClient;
    }

    /**
     * Defines the codenvy Client to use.
     */
    protected void setCodenvyClient(CodenvyClient codenvyClient) {
        this.codenvyClient = codenvyClient;
    }

    /**
     * @return the current workspace used at runtime
     */
    protected Workspace getCurrentWorkspace() {
        return (Workspace) session.get(Workspace.class.getName());
    }


    /**
     * @return the current project used at runtime
     */
    protected Project getCurrentProject() {
        return (Project) session.get(Project.class.getName());
    }


    /**
     * Defines the codenvy settings.
     * @param codenvySettings the settings that will replace the existing
     */
    protected void setCodenvySettings(Properties codenvySettings) {
        this.codenvySettings = codenvySettings;
    }

    /**
     * Helper method that checks if the user is currently logged or not and display an error if not logged.
     * @return the current Codenvy instance if one is found
     */
    protected Codenvy checkLoggedIn() {
        Codenvy codenvy = getCurrentCodenvy();

        // unset
        if (codenvy == null) {
            Ansi buffer = Ansi.ansi();

            buffer.fg(Ansi.Color.RED);
            buffer.a("Not logged in");
            buffer.fg(Ansi.Color.DEFAULT);
            session.getConsole().println(buffer.toString());
        }
        return codenvy;
    }

}