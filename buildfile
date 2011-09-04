Buildr.settings.build['scala.version'] = '2.9.1'
require 'buildr/scala'

repositories.remote << 'http://www.ibiblio.org/maven2/'

define 'scala_practice' do
  test.using(:scalatest)
end
