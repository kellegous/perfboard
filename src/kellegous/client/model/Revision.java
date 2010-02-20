/**
 * 
 */
package kellegous.client.model;

import kellegous.client.data.Date;
import kellegous.client.data.StringArray;

public class Revision {
  private final String m_revision;
  private final String m_author;
  private final StringArray m_message;
  private final Date m_date;

  Revision(String revision, String author, StringArray message, Date date) {
    m_revision = revision;
    m_author = author;
    m_message = message;
    m_date = date;
  }

  public String revision() {
    return m_revision;
  }

  public static String shortenAuthor(String author) {
    // TODO(knorton): What to do about random Googlers who are now committing?
    int index = author.indexOf("@google.com");
    if (index >= 0)
      return author.substring(0, index);

    index = author.indexOf("@fabbott-svn");
    if (index >= 0)
      return author.substring(0, index);

    if (author.indexOf("gwt.team.") == 0)
      return author.substring("gwt.team.".length());
    
    if (author.indexOf("gwt.mirrorbot@") == 0)
      return "MirrorBot";

    return author;
  }

  public String author() {
    return m_author;
  }

  public StringArray message() {
    return m_message;
  }

  public Date date() {
    return m_date;
  }
}