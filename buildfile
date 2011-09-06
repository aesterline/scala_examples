require 'buildr/scala'

repositories.remote << 'http://www.ibiblio.org/maven2/'

define 'scala_practice' do
  compile.with :scalaquery

  test.with :hsqldb
  test.using(:scalatest)
end
