package com.gandsys.inu.infra

import com.typesafe.config.Config
import akka.actor.{ActorSystem, Address}

import collection.JavaConverters._


object SeedNodes {

  def getList(implicit config: Config): Seq[String] = {
    config.getStringList("cluster.seed-nodes").asScala
  }

  def addresses(implicit config: Config, system: ActorSystem): List[Address] = {
    val port = config.getInt("akka.remote.netty.tcp.port")
    getList(config).map { node => new Address("akka.tcp", system.name, node.trim, port) } toList
  }
}
