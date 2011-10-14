package com.esterlines.scalaquery

// Import the session management, including the implicit threadLocalSession
import org.scalaquery.session._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.simple.{GetResult, DynamicQuery}

// Import the query language
import org.scalaquery.ql._

// Import the standard SQL types
import org.scalaquery.ql.TypeMapper._

// Use H2Driver which implements ExtendedProfile and thus requires ExtendedTables
import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.ql.extended.{ExtendedTable => Table}
import org.scalaquery.simple.StaticQuery._

/**
 * A simple example that uses statically typed queries against an in-memory
 * H2 database. The example data comes from Oracle's JDBC tutorial at
 * http://download.oracle.com/javase/tutorial/jdbc/basics/tables.html.
 */
object FirstExample {
  def main(args: Array[String]) {

    // Definition of the SUPPLIERS table
    val Suppliers = new Table[(Int, String, String, String, String, String)]("SUPPLIERS") {
      def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
      def name = column[String]("SUP_NAME", O.DBType("varchar(50)"))
      def street = column[String]("STREET", O.DBType("varchar(50)"))
      def city = column[String]("CITY", O.DBType("varchar(50)"))
      def state = column[String]("STATE", O.DBType("varchar(50)"))
      def zip = column[String]("ZIP", O.DBType("varchar(50)"))
      // Every table needs a * projection with the same type as the table's type parameter
      def * = id ~ name ~ street ~ city ~ state ~ zip
    }

    // Definition of the COFFEES table
    val Coffees = new Table[(String, Int, Double, Int, Int)]("COFFEES") {
      def name = column[String]("COF_NAME", O.PrimaryKey, O.DBType("varchar(50)"))
      def supID = column[Int]("SUP_ID")
      def price = column[Double]("PRICE")
      def sales = column[Int]("SALES")
      def total = column[Int]("TOTAL")
      def * = name ~ supID ~ price ~ sales ~ total
      // A reified foreign key relation that can be navigated to create a join
      def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id)
    }

    // Connect to the database and execute the following block within a session
    Database.forURL("jdbc:hsqldb:mem:test1", driver = "org.hsqldb.jdbcDriver") withSession {
      // The session is never named explicitly. It is bound to the current
      // thread as the threadLocalSession that we imported

      // Create the tables, including primary and foreign keys
      (Suppliers.ddl ++ Coffees.ddl).create

      // Insert some suppliers
      Suppliers.insert(101, "Acme, Inc.",      "99 Market Street", "Groundsville", "CA", "95199")
      Suppliers.insert( 49, "Superior Coffee", "1 Party Place",    "Mendocino",    "CA", "95460")
      Suppliers.insert(150, "The High Ground", "100 Coffee Lane",  "Meadows",      "CA", "93966")

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      Coffees.insertAll(
        ("Colombian",         101, 7.99, 0, 0),
        ("French_Roast",       49, 8.99, 0, 0),
        ("Espresso",          150, 9.99, 0, 0),
        ("Colombian_Decaf",   101, 8.99, 0, 0),
        ("French_Roast_Decaf", 49, 9.99, 0, 0)
      )

      class SimpleResult extends GetResult[List[(String, Any)]] {
        def apply(rs: PositionedResult) = {
          val buffer = new scala.collection.mutable.ListBuffer[(String, Any)]
          val resultSet = rs.rs
          val metaData = resultSet.getMetaData

          for(i <- 1 to metaData.getColumnCount) {
            buffer += new Tuple2[String, Any](metaData.getColumnName(i), resultSet.getObject(i))
          }
          buffer.toList
        }
      }

      val dynamicQuery = queryNA[List[(String, Any)]]("select COF_NAME, PRICE from coffees where SUP_ID = 101")(new SimpleResult)
      dynamicQuery.foreach { row =>
        row.foreach { c =>
        c match {
          case (a, b) => {print(a); println(b)}
        }
      }
      }


//      // Iterate through all coffees and output them
//      println("Coffees:")
//      Query(Coffees) foreach { case (name, supID, price, sales, total) =>
//        println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
//      }
//
//      // Perform a join to retrieve coffee names and supplier names for
//      // all coffees costing less than $9.00
//      println("Manual join:")
//      val q2 = for {
//        c <- Coffees if c.price < 9.0
//        s <- Suppliers if s.id === c.supID
//      } yield c.name ~ s.name
//      for(t <- q2) println("  " + t._1 + " supplied by " + t._2)
//
//      // Do the same thing using the navigable foreign key
//      println("Join by foreign key:")
//      val q3 = for {
//        c <- Coffees if c.price < 9.0
//        s <- c.supplier
//      } yield c.name ~ s.name
//      // This time we read the result set into a List
//      val l3: List[(String, String)] = q3.list
//      for((s1, s2) <- l3) println("  " + s1 + " supplied by " + s2)
//
//      // Check the SELECT statement for that query
//      println(q3.selectStatement)
//
//      // Compute the number of coffees by each supplier
//      println("Coffees per supplier:")
//      val q4 = for {
//        c <- Coffees
//        s <- c.supplier
//        _ <- Query groupBy s.id
//      } yield s.name.min.get ~ c.name.count
//      // .get is needed because ScalaQuery cannot enforce statically that
//      // the supplier is always available (being a non-nullable foreign key),
//      // thus wrapping it in an Option
//      q4 foreach { case (name, count) =>
//        println("  " + name + ": " + count)
//      }
    }
  }
}