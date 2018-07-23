<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\EmptyObject;

class EmptyObjectTest extends TestCase {

    public function testEmptyObjectInit(){
        $emptyObject = new EmptyObject();
        $this->assertNotNull( $emptyObject );
    }

}