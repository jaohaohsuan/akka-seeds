package com.gandsys.inu.infra

import com.typesafe.config.Config
import akka.actor.{ActorSystem, Address}
import collection.JavaConverters._

class ClusterConfig(val config: Config)  {

  val portNum = config.getInt("port")
  val clusterName = config.getString("name")

  lazy val addresses: List[Address] = {
    config.getStringList("seed-nodes").asScala.map { node => new Address("akka.tcp", clusterName, node.trim, portNum) } toList
  }
}


