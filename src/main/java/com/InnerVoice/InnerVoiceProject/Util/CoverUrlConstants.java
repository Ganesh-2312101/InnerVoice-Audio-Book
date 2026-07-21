package com.InnerVoice.InnerVoiceProject.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralised book cover URL maps.
 * Replaces the Python COVER_URLS dictionaries from:
 *   - download_and_update_local.py  (Amazon URLs)
 *   - update_covers.py              (Amazon URLs)
 *   - fix_images_instant.py         (Open Library ISBN URLs)
 */
public class CoverUrlConstants {

    // ── Amazon URLs ────────────────────────────────────────────────────────────
    public static final Map<String, String> AMAZON_COVER_URLS = new HashMap<>();

    static {
        // Productivity
        AMAZON_COVER_URLS.put("Atomic Habits",                       "https://m.media-amazon.com/images/I/81ANaVZk5LL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Deep Work",                           "https://m.media-amazon.com/images/I/81JJ7fyyKyS._SY466_.jpg");
        AMAZON_COVER_URLS.put("The 7 Habits of Highly Effective People", "https://m.media-amazon.com/images/I/71yI1MWsxFL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The 7 Habits",                        "https://m.media-amazon.com/images/I/71yI1MWsxFL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Eat That Frog",                       "https://m.media-amazon.com/images/I/71ozhMK0r4L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The 5 AM Club",                       "https://m.media-amazon.com/images/I/71zytzrg6lL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Getting Things Done",                 "https://m.media-amazon.com/images/I/71fEMHDdDNL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Think and Grow Rich",                 "https://m.media-amazon.com/images/I/71UypkUjStL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The One Thing",                       "https://m.media-amazon.com/images/I/71eRK-bvDvL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Mindset",                             "https://m.media-amazon.com/images/I/61bDwEBtdFL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Power of Habit",                  "https://m.media-amazon.com/images/I/81Myap0MmhL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Rich Dad Poor Dad",                   "https://m.media-amazon.com/images/I/81bsw6fnUiL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Magic of Thinking Big",           "https://m.media-amazon.com/images/I/71UfNWfSr0L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Compound Effect",                 "https://m.media-amazon.com/images/I/71HAiKFOdML._SY466_.jpg");
        AMAZON_COVER_URLS.put("Essentialism",                        "https://m.media-amazon.com/images/I/71Iyf-FE4ZL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Miracle Morning",                 "https://m.media-amazon.com/images/I/71p0KQh37sL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Success Principles",              "https://m.media-amazon.com/images/I/81r7Os3CJPL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Start With Why",                      "https://m.media-amazon.com/images/I/71NCtBz4VcL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Grit",                                "https://m.media-amazon.com/images/I/81VEKXfnUAL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Outliers",                            "https://m.media-amazon.com/images/I/71m-MxdJ2WL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Drive",                               "https://m.media-amazon.com/images/I/614St3EEsxL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Ikigai",                              "https://m.media-amazon.com/images/I/81l3rZK4lnL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Unlimited Power",                     "https://m.media-amazon.com/images/I/71W14IjvRVL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Can't Hurt Me",                       "https://m.media-amazon.com/images/I/81gTRv2HXrL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Peak",                                "https://m.media-amazon.com/images/I/71V0Vu-oLkL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Make Time",                           "https://m.media-amazon.com/images/I/71gLEoj1woL._SY466_.jpg");

        // Science Fiction
        AMAZON_COVER_URLS.put("Dune",                                "https://m.media-amazon.com/images/I/81ym3QUd3KL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Foundation",                          "https://m.media-amazon.com/images/I/81iC6e0TQ+L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Martian",                         "https://m.media-amazon.com/images/I/81wFMY9OAFL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Time Machine",                    "https://m.media-amazon.com/images/I/71Qai3nMT3L._SY466_.jpg");
        AMAZON_COVER_URLS.put("Ender's Game",                        "https://m.media-amazon.com/images/I/91LHLZ+BTRL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Ready Player One",                    "https://m.media-amazon.com/images/I/91urHoL1JmL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Neuromancer",                         "https://m.media-amazon.com/images/I/81VEKYnbcaL._SY466_.jpg");
        AMAZON_COVER_URLS.put("I Robot",                             "https://m.media-amazon.com/images/I/51ee7WMx-gL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Fahrenheit 451",                      "https://m.media-amazon.com/images/I/71OFqSRFDgL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Brave New World",                     "https://m.media-amazon.com/images/I/81zE42gT3xL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Snow Crash",                          "https://m.media-amazon.com/images/I/81Bsa3tFKQL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Hyperion",                            "https://m.media-amazon.com/images/I/51DE2NqFPOL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Childhood's End",                     "https://m.media-amazon.com/images/I/71cNSsCqt6L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Forever War",                     "https://m.media-amazon.com/images/I/81MKG5WoqlL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Solaris",                             "https://m.media-amazon.com/images/I/71bMfxafp3L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Expanse",                         "https://m.media-amazon.com/images/I/91OI9tPW-5L._SY466_.jpg");
        AMAZON_COVER_URLS.put("Red Rising",                          "https://m.media-amazon.com/images/I/81bcRVRjwjL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Project Hail Mary",                   "https://m.media-amazon.com/images/I/91vS2L5670L._SY466_.jpg");
        AMAZON_COVER_URLS.put("Ringworld",                           "https://m.media-amazon.com/images/I/81ep0gOfrKL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Left Hand of Darkness",           "https://m.media-amazon.com/images/I/61-Iq4cEJSL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Do Androids Dream",                   "https://m.media-amazon.com/images/I/71HavnzUkXL._SY466_.jpg");
        AMAZON_COVER_URLS.put("2001 A Space Odyssey",                "https://m.media-amazon.com/images/I/71b06MkyScL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Moon is a Harsh Mistress",        "https://m.media-amazon.com/images/I/91c8wDKkJ1L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Three Body Problem",              "https://m.media-amazon.com/images/I/919XM42JQlL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Invisible Man",                   "https://m.media-amazon.com/images/I/71lp3U8cJXL._SY466_.jpg");

        // Crime Thrillers
        AMAZON_COVER_URLS.put("Sherlock Holmes",                     "https://m.media-amazon.com/images/I/81z1yJRbHXL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Gone Girl",                           "https://m.media-amazon.com/images/I/71+GQJT2gkL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Da Vinci Code",                   "https://m.media-amazon.com/images/I/91Q5dCjc2KL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Silent Patient",                  "https://m.media-amazon.com/images/I/81IzbD2IiIL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Murder on the Orient Express",        "https://m.media-amazon.com/images/I/81pqpOQW8nL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Firm",                            "https://m.media-amazon.com/images/I/81prqJYKGiL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Client",                          "https://m.media-amazon.com/images/I/81M-ZZh0tTL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Killing Floor",                       "https://m.media-amazon.com/images/I/81TbD38YOEL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Girl with the Dragon Tattoo",     "https://m.media-amazon.com/images/I/81iqZ2HHD-L._SY466_.jpg");
        AMAZON_COVER_URLS.put("Angels and Demons",                   "https://m.media-amazon.com/images/I/81WcnNQ-TBL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Inferno",                             "https://m.media-amazon.com/images/I/81GDME0FU+L._SY466_.jpg");
        AMAZON_COVER_URLS.put("Origin",                              "https://m.media-amazon.com/images/I/91CQHL+HUKL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Digital Fortress",                    "https://m.media-amazon.com/images/I/71NFGlFEs8L._SY466_.jpg");
        AMAZON_COVER_URLS.put("Presumed Innocent",                   "https://m.media-amazon.com/images/I/81JDvDShyBL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Appeal",                          "https://m.media-amazon.com/images/I/81Pv8sBnrfL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Broker",                          "https://m.media-amazon.com/images/I/814tC6QRxbL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Partner",                         "https://m.media-amazon.com/images/I/71g2MkaB+dL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Pelican Brief",                   "https://m.media-amazon.com/images/I/71J0y-mOR5L._SY466_.jpg");
        AMAZON_COVER_URLS.put("A Time To Kill",                      "https://m.media-amazon.com/images/I/81NHDh8ZjWL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Runaway Jury",                    "https://m.media-amazon.com/images/I/81TBfLCclKL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Last Juror",                      "https://m.media-amazon.com/images/I/91e2W7UBG7L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Testament",                       "https://m.media-amazon.com/images/I/71fXjWMO-aL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Reversal",                        "https://m.media-amazon.com/images/I/818+ILbTDyL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Racketeer",                       "https://m.media-amazon.com/images/I/81mNE6KKOEL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Whistler",                        "https://m.media-amazon.com/images/I/81YkqyaFVEL._SY466_.jpg");

        // Novels
        AMAZON_COVER_URLS.put("The Alchemist",                       "https://m.media-amazon.com/images/I/71aFt4+OTOL._SY466_.jpg");
        AMAZON_COVER_URLS.put("1984",                                "https://m.media-amazon.com/images/I/71kxa1-0mfL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Pride and Prejudice",                 "https://m.media-amazon.com/images/I/71Q1tPupKjL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Hobbit",                          "https://m.media-amazon.com/images/I/71jLBXtWJWL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Great Gatsby",                    "https://m.media-amazon.com/images/I/81QuEGw8VPL._SY466_.jpg");
        AMAZON_COVER_URLS.put("To Kill a Mockingbird",               "https://m.media-amazon.com/images/I/81gepf1eMqL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Kite Runner",                     "https://m.media-amazon.com/images/I/81LVDX01+6L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Book Thief",                      "https://m.media-amazon.com/images/I/91biFe+QXOL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Little Women",                        "https://m.media-amazon.com/images/I/81T7l6cS6AL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Fault in Our Stars",              "https://m.media-amazon.com/images/I/817tHNcyAgS._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Catcher in the Rye",              "https://m.media-amazon.com/images/I/8125BDk3l9L._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Hunger Games",                    "https://m.media-amazon.com/images/I/71un2hI4mcL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Life of Pi",                          "https://m.media-amazon.com/images/I/71XPa2bFijL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Animal Farm",                         "https://m.media-amazon.com/images/I/71je3-DsQEL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Jane Eyre",                           "https://m.media-amazon.com/images/I/71hMqFLzrLL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Dracula",                             "https://m.media-amazon.com/images/I/91M8resLqUL._SY466_.jpg");
        AMAZON_COVER_URLS.put("Frankenstein",                        "https://m.media-amazon.com/images/I/81z7E0uB5sL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Giver",                           "https://m.media-amazon.com/images/I/51SKzF942pL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Outsiders",                       "https://m.media-amazon.com/images/I/91HPG31dTiL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Road",                            "https://m.media-amazon.com/images/I/51M7XGLQTBL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Shack",                           "https://m.media-amazon.com/images/I/81XZ3MJPoDL._SY466_.jpg");
        AMAZON_COVER_URLS.put("A Walk to Remember",                  "https://m.media-amazon.com/images/I/81Jqkk1MxRL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Help",                            "https://m.media-amazon.com/images/I/71wBXJCEhjL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Old Man and the Sea",             "https://m.media-amazon.com/images/I/71OZY035QKL._SY466_.jpg");
        AMAZON_COVER_URLS.put("The Little Prince",                   "https://m.media-amazon.com/images/I/71OZY035QKL._SY466_.jpg");
    }

    // ── Open Library ISBN URLs ─────────────────────────────────────────────────
    public static final Map<String, String> OPEN_LIBRARY_COVER_URLS = new HashMap<>();

    static {
        // Productivity
        OPEN_LIBRARY_COVER_URLS.put("Atomic Habits",                       "https://covers.openlibrary.org/b/isbn/9780735211292-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Deep Work",                           "https://covers.openlibrary.org/b/isbn/9781455586691-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The 7 Habits of Highly Effective People", "https://covers.openlibrary.org/b/isbn/9781982137274-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The 7 Habits",                        "https://covers.openlibrary.org/b/isbn/9781982137274-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Eat That Frog",                       "https://covers.openlibrary.org/b/isbn/9781626569416-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The 5 AM Club",                       "https://covers.openlibrary.org/b/isbn/9781443456623-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Getting Things Done",                 "https://covers.openlibrary.org/b/isbn/9780143126560-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Think and Grow Rich",                 "https://covers.openlibrary.org/b/isbn/9781585424337-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The One Thing",                       "https://covers.openlibrary.org/b/isbn/9781885167774-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Mindset",                             "https://covers.openlibrary.org/b/isbn/9780345472328-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Power of Habit",                  "https://covers.openlibrary.org/b/isbn/9781400069286-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Rich Dad Poor Dad",                   "https://covers.openlibrary.org/b/isbn/9781612680194-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Magic of Thinking Big",           "https://covers.openlibrary.org/b/isbn/9780671646783-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Compound Effect",                 "https://covers.openlibrary.org/b/isbn/9781593157135-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Essentialism",                        "https://covers.openlibrary.org/b/isbn/9780804137386-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Miracle Morning",                 "https://covers.openlibrary.org/b/isbn/9780979019715-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Success Principles",              "https://covers.openlibrary.org/b/isbn/9780060594893-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Start With Why",                      "https://covers.openlibrary.org/b/isbn/9781591846444-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Grit",                                "https://covers.openlibrary.org/b/isbn/9781501111105-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Outliers",                            "https://covers.openlibrary.org/b/isbn/9780316017930-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Drive",                               "https://covers.openlibrary.org/b/isbn/9781594484803-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Ikigai",                              "https://covers.openlibrary.org/b/isbn/9780143130727-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Unlimited Power",                     "https://covers.openlibrary.org/b/isbn/9780684845777-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Can't Hurt Me",                       "https://covers.openlibrary.org/b/isbn/9781544512273-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Peak",                                "https://covers.openlibrary.org/b/isbn/9780544456235-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Make Time",                           "https://covers.openlibrary.org/b/isbn/9780525572428-L.jpg");

        // Science Fiction
        OPEN_LIBRARY_COVER_URLS.put("Dune",                                "https://covers.openlibrary.org/b/isbn/9780441172719-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Foundation",                          "https://covers.openlibrary.org/b/isbn/9780553293357-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Martian",                         "https://covers.openlibrary.org/b/isbn/9780553418026-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Time Machine",                    "https://covers.openlibrary.org/b/isbn/9780486284729-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Ender's Game",                        "https://covers.openlibrary.org/b/isbn/9780812550702-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Ready Player One",                    "https://covers.openlibrary.org/b/isbn/9780307887443-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Neuromancer",                         "https://covers.openlibrary.org/b/isbn/9780441569595-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("I Robot",                             "https://covers.openlibrary.org/b/isbn/9780553294385-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Fahrenheit 451",                      "https://covers.openlibrary.org/b/isbn/9781451673319-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Brave New World",                     "https://covers.openlibrary.org/b/isbn/9780060850524-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Snow Crash",                          "https://covers.openlibrary.org/b/isbn/9780553380958-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Hyperion",                            "https://covers.openlibrary.org/b/isbn/9780553283686-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Childhood's End",                     "https://covers.openlibrary.org/b/isbn/9780345347954-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Forever War",                     "https://covers.openlibrary.org/b/isbn/9780312536633-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Solaris",                             "https://covers.openlibrary.org/b/isbn/9780156027601-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Expanse",                         "https://covers.openlibrary.org/b/isbn/9780316129084-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Red Rising",                          "https://covers.openlibrary.org/b/isbn/9780345539786-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Project Hail Mary",                   "https://covers.openlibrary.org/b/isbn/9780593135204-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Ringworld",                           "https://covers.openlibrary.org/b/isbn/9780345333926-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Left Hand of Darkness",           "https://covers.openlibrary.org/b/isbn/9780441478125-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Do Androids Dream",                   "https://covers.openlibrary.org/b/isbn/9780345404473-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("2001 A Space Odyssey",                "https://covers.openlibrary.org/b/isbn/9780451457998-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Moon is a Harsh Mistress",        "https://covers.openlibrary.org/b/isbn/9780312863555-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Three Body Problem",              "https://covers.openlibrary.org/b/isbn/9780765382030-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Invisible Man",                   "https://covers.openlibrary.org/b/isbn/9780486270715-L.jpg");

        // Crime Thrillers
        OPEN_LIBRARY_COVER_URLS.put("Sherlock Holmes",                     "https://covers.openlibrary.org/b/isbn/9780140439083-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Gone Girl",                           "https://covers.openlibrary.org/b/isbn/9780307588371-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Da Vinci Code",                   "https://covers.openlibrary.org/b/isbn/9780307474278-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Silent Patient",                  "https://covers.openlibrary.org/b/isbn/9781250301697-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Murder on the Orient Express",        "https://covers.openlibrary.org/b/isbn/9780062693662-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Firm",                            "https://covers.openlibrary.org/b/isbn/9780440245926-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Client",                          "https://covers.openlibrary.org/b/isbn/9780440213529-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Killing Floor",                       "https://covers.openlibrary.org/b/isbn/9780515153651-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Girl with the Dragon Tattoo",     "https://covers.openlibrary.org/b/isbn/9780307454546-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Angels and Demons",                   "https://covers.openlibrary.org/b/isbn/9781416524793-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Inferno",                             "https://covers.openlibrary.org/b/isbn/9781400079155-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Origin",                              "https://covers.openlibrary.org/b/isbn/9780385514231-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Digital Fortress",                    "https://covers.openlibrary.org/b/isbn/9780312944926-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Presumed Innocent",                   "https://covers.openlibrary.org/b/isbn/9780446350983-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Appeal",                          "https://covers.openlibrary.org/b/isbn/9780385342926-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Broker",                          "https://covers.openlibrary.org/b/isbn/9780440241584-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Partner",                         "https://covers.openlibrary.org/b/isbn/9780440224761-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Pelican Brief",                   "https://covers.openlibrary.org/b/isbn/9780440214052-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("A Time To Kill",                      "https://covers.openlibrary.org/b/isbn/9780440211723-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Runaway Jury",                    "https://covers.openlibrary.org/b/isbn/9780440221470-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Last Juror",                      "https://covers.openlibrary.org/b/isbn/9780440241577-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Testament",                       "https://covers.openlibrary.org/b/isbn/9780440234746-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Reversal",                        "https://covers.openlibrary.org/b/isbn/9780316069489-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Racketeer",                       "https://covers.openlibrary.org/b/isbn/9780345530561-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Whistler",                        "https://covers.openlibrary.org/b/isbn/9781101967683-L.jpg");

        // Novels
        OPEN_LIBRARY_COVER_URLS.put("The Alchemist",                       "https://covers.openlibrary.org/b/isbn/9780062315007-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("1984",                                "https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Pride and Prejudice",                 "https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Hobbit",                          "https://covers.openlibrary.org/b/isbn/9780547928227-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Great Gatsby",                    "https://covers.openlibrary.org/b/isbn/9780743273565-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("To Kill a Mockingbird",               "https://covers.openlibrary.org/b/isbn/9780060935467-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Kite Runner",                     "https://covers.openlibrary.org/b/isbn/9781594631931-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Book Thief",                      "https://covers.openlibrary.org/b/isbn/9780375842207-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Little Women",                        "https://covers.openlibrary.org/b/isbn/9780147514011-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Fault in Our Stars",              "https://covers.openlibrary.org/b/isbn/9780525478812-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Catcher in the Rye",              "https://covers.openlibrary.org/b/isbn/9780316769488-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Hunger Games",                    "https://covers.openlibrary.org/b/isbn/9780439023481-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Life of Pi",                          "https://covers.openlibrary.org/b/isbn/9780156027328-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Animal Farm",                         "https://covers.openlibrary.org/b/isbn/9780451526342-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Jane Eyre",                           "https://covers.openlibrary.org/b/isbn/9780141441146-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Dracula",                             "https://covers.openlibrary.org/b/isbn/9780141439846-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("Frankenstein",                        "https://covers.openlibrary.org/b/isbn/9780141439471-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Giver",                           "https://covers.openlibrary.org/b/isbn/9780544336261-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Outsiders",                       "https://covers.openlibrary.org/b/isbn/9780142407332-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Road",                            "https://covers.openlibrary.org/b/isbn/9780307387899-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Shack",                           "https://covers.openlibrary.org/b/isbn/9780964729230-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("A Walk to Remember",                  "https://covers.openlibrary.org/b/isbn/9780446693806-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Help",                            "https://covers.openlibrary.org/b/isbn/9780399155345-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Old Man and the Sea",             "https://covers.openlibrary.org/b/isbn/9780684801223-L.jpg");
        OPEN_LIBRARY_COVER_URLS.put("The Little Prince",                   "https://covers.openlibrary.org/b/isbn/9780156012195-L.jpg");
    }

    private CoverUrlConstants() {}
}
