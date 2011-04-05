/*
 * (c) Copyright 2011 Epimorphics Ltd.
 * All rights reserved.
 * [See end of file]
 */

package tx;

import java.util.Iterator ;

import org.openjena.atlas.lib.Bytes ;
import org.openjena.atlas.logging.Log ;

import com.hp.hpl.jena.tdb.base.block.BlockMgr ;
import com.hp.hpl.jena.tdb.base.block.BlockMgrFactory ;
import com.hp.hpl.jena.tdb.base.record.Record ;
import com.hp.hpl.jena.tdb.base.record.RecordFactory ;
import com.hp.hpl.jena.tdb.index.bplustree.BPlusTree ;
import com.hp.hpl.jena.tdb.index.bplustree.BPlusTreeParams ;

public class TxMain
{
    /*
     * read oeprations not called sesssion ops at all.
     * reads not doing a release
     * BPlusTreeWrapper.
     * Iterator tracking?
     * End transaction => close all and any open iterators.
     *   BPlusTree.replicate(BlockMgr1, BlocMgr2)
     */
    
    static { Log.setLog4j() ; }
    
    public static void main(String... args)
    {
        RecordFactory rf = new RecordFactory(8,8) ;
        int order = 2 ;
        BPlusTreeParams params = new BPlusTreeParams(order, rf) ;
        System.out.println(params) ;
        int blockSize  = BPlusTreeParams.calcBlockSize(order, rf) ;
        System.out.println("Block size = "+blockSize) ;
        BlockMgr mgr1 = BlockMgrFactory.createMem("B1", blockSize) ;
        mgr1 = new BlockMgrTracker("B1", mgr1) ;
        
        BlockMgr mgr2 = BlockMgrFactory.createMem("B2", blockSize) ;
        mgr2 = new BlockMgrTracker("B2", mgr2) ;
        BPlusTree bpt = BPlusTree.attach(params, mgr1, mgr2) ;
        
        for ( int i = 0 ; i < 4 ; i++ ) 
        {
            Record r = record(rf, i+0x100000L, i+0x90000000L) ;
            bpt.add(r) ;
        }

        System.out.println() ;
        
        Record r = record(rf, 3+0x100000L, 0) ;
        r = bpt.find(r) ;
        System.out.println("Find: "+r) ;
        System.out.println() ;
        
        Iterator<Record> iter = bpt.iterator() ;
        for ( ; iter.hasNext() ; )
            System.out.println(iter.next()) ;
        System.out.println() ;
        bpt.dump() ;
    }

    static Record record(RecordFactory rf, long key, long val)
    {
        Record r = rf.create() ;
        Bytes.setLong(key, r.getKey()) ;
        Bytes.setLong(val, r.getValue()) ;
        return r ;
    }
}

/*
 * (c) Copyright 2011 Epimorphics Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */