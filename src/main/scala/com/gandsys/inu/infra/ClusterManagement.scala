package com.gandsys.inu.infra

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class ClusterManagement(val cluster: Cluster)(implicit ec: ExecutionContext) {

  private val log = com.typesafe.scalalogging.Logger(this.getClass)

  val clusterHttpMan = ClusterHttpManagement(cluster)

  def register() = {
    clusterHttpMan.start().onComplete { _ =>
      log.info("ClusterHttpManagement is up")
      sys.addShutdownHook {
        clusterHttpMan.stop().onComplete { _ =>
          log.info("ClusterHttpManagement stopped.")
        }
      }
    }
  }

  def waitTerminated(implicit system: ActorSystem) = {
    Await.result(system.whenTerminated, Duration.Inf)
    log.info(s"${system.name} has shutdown gracefully")
  }

  def gracefullyLeaveCluster(implicit cluster: Cluster): Unit = {
    cluster.leave(cluster.selfAddress)
    cluster.down(cluster.selfAddress)
  }
}
