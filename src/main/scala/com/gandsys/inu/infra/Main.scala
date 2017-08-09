package com.gandsys.inu.infra

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.typesafe.config.{Config, ConfigFactory}

object Main extends App {
  
  val log = com.typesafe.scalalogging.Logger(this.getClass)

  private val config: Config = ConfigFactory.load()
  private val clusterCfg = new ClusterConfig(config.getConfig("cluster"))

  log.debug(s"akka.remote.netty.tcp.port = ${config.getInt("akka.remote.netty.tcp.port")}")
  log.debug(s"akka.remote.netty.tcp.hostname = ${config.getString("akka.remote.netty.tcp.hostname")}")

  implicit val system = ActorSystem(clusterCfg.clusterName)
  implicit val ec = system.dispatcher

  implicit val cluster: Cluster = Cluster(system)
  cluster.joinSeedNodes(clusterCfg.addresses)

  val cm = new ClusterManagement(cluster)
  cm.register()

  sys.addShutdownHook {
    cm.gracefullyLeaveCluster
    cm.waitTerminated
  }
}
