package com.gandsys.inu.infra

import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement

import scala.concurrent.{ExecutionContext}

class ClusterManagement(val cluster: Cluster) {

  private val log = com.typesafe.scalalogging.Logger(this.getClass)

  val clusterHttpMan = ClusterHttpManagement(cluster)

  def register()(implicit ec: ExecutionContext) = {
    clusterHttpMan.start().onComplete { _ =>
      log.info("ClusterHttpManagement is up")
      sys.addShutdownHook {
        clusterHttpMan.stop().onComplete { _ =>
          log.info("ClusterHttpManagement stopped.")
        }
      }
    }
  }

  def gracefullyLeaveCluster: Unit = {
    cluster.leave(cluster.selfAddress)
    cluster.down(cluster.selfAddress)
  }
}
