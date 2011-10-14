require 'buildr/scala'

repositories.remote << 'http://www.ibiblio.org/maven2/'

define 'scala_examples' do
  compile.with :scalaquery, :jline

  test.with :hsqldb
  test.using(:scalatest)
end
