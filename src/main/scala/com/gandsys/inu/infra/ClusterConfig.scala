package com.gandsys.inu.infra

import com.typesafe.config.Config
import akka.actor.{ActorSystem, Address}

import collection.JavaConverters._


class ClusterConfig(val config: Config)(implicit system: ActorSystem)  {

  val portNum = config.getInt("port")

  lazy val addresses: List[Address] = {
    config.getStringList("seed-nodes").asScala.map { node => new Address("akka.tcp", system.name, node.trim, portNum) } toList
  }
}


