/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.analytics;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Modify analytics.properties:
 * analytics.mongodb.embedded=false
 * analytics.mongodb.url=mongodb://localhost:27017/organization
 *
 * @author Anatoliy Bazko
 */
public class TestRestoreUserProfiles extends BaseTest {
    private static final String LDAP_DUMP = "/home/tolusha/ldap.diff";

    private BufferedWriter writer;

    @BeforeMethod
    public void setUp() throws Exception {
        File fileOut = new File(BASE_DIR, "profiles.log");
        writer = new BufferedWriter(new FileWriter(fileOut));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        writer.close();
    }

    @Test
    public void restore() throws Exception {
        Map<String, String> emails = readUsersEmails();
        DBCollection profiles = mongoDb.getCollection("profiles");

        DBCursor cursor = profiles.find();
        while (cursor.hasNext()) {
            DBObject profile = cursor.next();
            String userId = (String)profile.get("userId");
            if (userId == null) {
                continue;
            }
            BasicDBList list = (BasicDBList)profile.get("attributes");

            Profile userProfile = new Profile();
            for (Object o : list) {
                DBObject attribute = (DBObject)o;

                String name = (String)attribute.get("name");
                String value = (String)attribute.get("value");

                switch (name) {
                    case "jobtitle":
                        try {
                            Integer.valueOf(value);
                            userProfile.jobtitle = "Other";
                        } catch (NumberFormatException e) {
                            userProfile.jobtitle = value;
                        }
                        break;
                    case "lastName":
                        userProfile.lastName = value;
                        break;
                    case "firstName":
                        userProfile.firstName = value;
                        break;
                    case "phone":
                        userProfile.phone = value;
                        break;
                    case "employer":
                        userProfile.company = value;
                        break;
                }
            }

            userProfile.email = emails.get(userId);
            if (userProfile.email == null || userProfile.email.isEmpty()) {
                LOG.warn("There is no email for " + userId);
            } else if (userProfile.email.toUpperCase().startsWith("ANONYMOUSUSER")) {
                continue;
            }

            write(userProfile);
        }
    }

    private Map<String, String> readUsersEmails() throws IOException {
        Map<String, String> result = new HashMap<>();

        Email email = new Email();
        try (BufferedReader reader = new BufferedReader(new FileReader(LDAP_DUMP))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("dn: ")) {
                    result.put(email.userId, email.email);
                    email = new Email();
                } else if (line.startsWith("mail: ")) {
                    email.email = line.substring(6);
                } else if (line.startsWith("uid: ")) {
                    email.userId = line.substring(5);
                }
            }
        }

        return result;
    }

    private void write(Profile profile) throws IOException {
        writer.write("127.0.0.1 2013-02-01 00:00:01,000[l-4-thread-8211]  [INFO ] [Main 224]  [][][] - ");
        writer.write("EVENT#user-update-profile# " +
                     "USER#" + profile.email + "# " +
                     "FIRSTNAME#" + profile.firstName + "# " +
                     "LASTNAME#" + profile.lastName + "# " +
                     "COMPANY#" + profile.company + "# " +
                     "PHONE#" + profile.phone + "# " +
                     "JOBTITLE#" + profile.jobtitle + "#");
        writer.newLine();
    }

    @BeforeClass
    @Override
    public void clearDatabase() {
    }

    private static class Email {
        String email  = "";
        String userId = "";
    }

    private static class Profile {
        String firstName = "";
        String lastName  = "";
        String company   = "";
        String jobtitle  = "";
        String email     = "";
        String phone     = "";
    }
}
