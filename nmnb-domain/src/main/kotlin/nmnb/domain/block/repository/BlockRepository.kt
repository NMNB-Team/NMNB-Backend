package nmnb.domain.block.repository

import nmnb.domain.block.Block
import org.springframework.data.jpa.repository.JpaRepository

interface BlockRepository : JpaRepository<Block, Long>
