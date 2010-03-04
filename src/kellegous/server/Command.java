/**
 * 
 */
package kellegous.server;

import elemental.json.JsonValue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


interface Command {
  JsonValue execute(PathArgs args, HttpServletRequest req, HttpServletResponse res) throws Exception;
}