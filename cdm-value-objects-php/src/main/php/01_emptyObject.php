<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\EmptyObject;
use org\generated\json\EmptyObjectReader;

class EmptyObjectTest extends TestCase {

    public function testEmptyObjectInit(){
        $emptyObject = new EmptyObject();
        $this->assertNotNull( $emptyObject );
    }

    public function testWrite(){
        $reader = new EmptyObjectReader();
        $object = $reader->read( "{}" );
        $this->assertNotNull( $object );
    }

}