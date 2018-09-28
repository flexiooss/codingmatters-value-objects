<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\EmptyObject;
use org\generated\json\EmptyObjectReader;
use org\generated\json\EmptyObjectWriter;

class EmptyObjectTest extends TestCase {

    public function testEmptyObjectInit(){
        $emptyObject = new EmptyObject();
        $this->assertNotNull( $emptyObject );
    }

    public function testRead(){
        $reader = new EmptyObjectReader();
        $object = $reader->read( "{}" );
        $this->assertNotNull( $object );
    }

    public function testWrite(){
        $emptyObject = new EmptyObject();
        $writer = new EmptyObjectWriter();
        $json = $writer->write( $emptyObject );
        $this->assertSame( $json, "{}" );
    }

}