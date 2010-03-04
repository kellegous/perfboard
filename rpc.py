import httplib
import simplejson
import urlparse

class ClientException(Exception):
  def __init__(self, message):
    self.message = message

class Client:
  def __init__(self, url):
    # TODO(knorton): Fix this.
    assert not url.endswith("/")
    self._url = url

  def _DatetimeToArray(self, dt):
    return [dt.year, dt.month, dt.day, dt.hour, dt.minute, dt.second]

  def _Invoke(self, url, payload = None):
    url = urlparse.urlparse(url)
    conn = httplib.HTTPConnection(url.hostname, url.port)
    if payload is None:
      conn.request('GET', url.path)
    else:
      conn.request('POST', url.path, simplejson.dumps(payload))
    resp = conn.getresponse()
    if resp.status != 200:
      raise ClientException("HTTP status %d" % resp.status)
    return simplejson.loads(resp.read())
      
  def GetLatestRevision(self):
    return self._Invoke(url = "%s/get-head-revision" % self._url)

  def RecordRevision(self, id, author, message, date):
    print "Saving %s" % id
    status = self._Invoke(url = '%s/record-revision' % self._url,
        payload = {
          'id' : id,
          'author' : author,
          'message' : message,
          'date' : self._DatetimeToArray(date)})
    return status['ok?']
