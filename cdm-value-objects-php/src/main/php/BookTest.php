<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\Book;
use org\generated\Person;

class BookTest extends TestCase {

    public function testPerson() {
        $person = new Person();
        $person -> withName( "personName" )
                -> withEmail( "person@mail.con" );

        $this->assertSame( "personName", $person->name() );
        $this->assertSame( "person@mail.con", $person->email() );

    }

    public function testBook() {
        $book = new Book();

        $person = new Person();
        $person -> withName( "personName" )
                -> withEmail( "person@mail.con" );

        $book -> withName("bookName")
              -> withAuthor( $person )
              -> withBookFormat( "nul" );

        $this->assertSame( "bookName", $book->name() );
        $this->assertSame( "nul", $book->bookFormat() );
        $this->assertSame( "personName", $book->author()->name() );
        $this->assertSame( "person@mail.con", $book->author()->email() );
    }

}